package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.NewUserDTO;
import AlessiaMonasteri.BagECommerce.DTO.NewUserResponseDTO;
import AlessiaMonasteri.BagECommerce.DTO.UpdateUserRoleDTO;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Role;
import AlessiaMonasteri.BagECommerce.exceptions.ConflictException;
import AlessiaMonasteri.BagECommerce.exceptions.NotFoundException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.UserRepository;
import AlessiaMonasteri.BagECommerce.tools.MailgunSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcrypt;
    private final MailgunSender mailgunSender;

    public UserService(UserRepository userRepository, PasswordEncoder bcrypt, MailgunSender mailgunSender) {
        this.userRepository = userRepository;
        this.bcrypt = bcrypt;
        this.mailgunSender = mailgunSender;
    }

    // Lista di tutti gli utenti
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Recupera un utente tramite l'email
    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The user with email address " + email + " was not found."));
    }

    // Recupera un utente tramite ID
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userId));
    }

    // Registra un nuovo utente
    // valida i dati, verifica l'unicità della mail, cifra la password con BCrypt,
    // salva l’utente e invia l’email di registrazione
    public NewUserResponseDTO save(NewUserDTO body) {
        if (body == null) throw new ValidationException("Payload is required");

        if (body.name() == null || body.name().trim().length() < 2) {
            throw new ValidationException("The name cannot be shorter than 2 characters");
        }

        if (body.email() == null || body.email().isBlank()) {
            throw new ValidationException("Email is required");
        }

        if (userRepository.existsByEmailIgnoreCase(body.email())) {
            throw new ConflictException("The email " + body.email() + " is already in use.");
        }

        if (body.password() == null || body.password().isBlank()) {
            throw new ValidationException("Password is required");
        }

        User newUser = new User(
                body.name(),
                body.surname(),
                body.email(),
                bcrypt.encode(body.password())
        );

        newUser.setRole(Role.USER);
        newUser.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(newUser);

        try {
            mailgunSender.sendRegistrationEmail(saved);
        } catch (Exception ex) {
            // non blocca la registrazione se Mailgun fallisce
            System.out.println("Registration email not sent: " + ex.getMessage());
        }

        return new NewUserResponseDTO(saved.getId());
    }

    // Elimina un utente dato il suo ID
    public void findByIdAndDelete(UUID userId) {
        User found = findById(userId);
        userRepository.delete(found);
    }

    // Restituisce tutti gli utenti che hanno un determinato ruolo
    public List<User> findUsersByRole(Role role) {
        if (role == null) {
            throw new ValidationException("Role is required");
        }
        return userRepository.findByRole(role);
    }

    // Aggiorna i dati del profilo dell’utente autenticato (self-update)
    public User updateSelf(UUID currentUserId, NewUserDTO body) {
        if (body == null) throw new ValidationException("Payload is required");

        User found = findById(currentUserId);

        if (body.name() != null) {
            if (body.name().trim().length() < 2) {
                throw new ValidationException("The name cannot be shorter than 2 characters");
            }
            found.setName(body.name());
        }

        if (body.surname() != null) {
            found.setSurname(body.surname());
        }

        // L'email viene aggiornata solo se cambia e non è già usata
        if (body.email() != null && !body.email().equalsIgnoreCase(found.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(body.email())) {
                throw new ConflictException("The email " + body.email() + " is already in use.");
            }
            found.setEmail(body.email());
        }

        return userRepository.save(found);
    }

    // Aggiorna il ruolo di un utente impedendo la rimozione dell’ultimo SUPERADMIN
    public User updateUserRole(UUID userId, UpdateUserRoleDTO dto) {
        if (dto == null) throw new ValidationException("Payload is required");

        User target = findById(userId);

        Role newRole = dto.role();
        if (newRole == null) throw new ValidationException("Role is required");

        if (target.getRole() == Role.SUPERADMIN && newRole != Role.SUPERADMIN) {
            long superAdmins = userRepository.countByRole(Role.SUPERADMIN);
            if (superAdmins <= 1) throw new ValidationException("Cannot demote the last SUPERADMIN");
        }

        target.setRole(newRole);
        return userRepository.save(target);
    }
}