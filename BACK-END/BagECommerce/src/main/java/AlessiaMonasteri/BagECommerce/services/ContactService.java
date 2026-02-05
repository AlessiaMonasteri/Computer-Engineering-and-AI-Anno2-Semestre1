package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.ContactCreateDTO;
import AlessiaMonasteri.BagECommerce.entities.Contact;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Subject;
import AlessiaMonasteri.BagECommerce.exceptions.NotFoundException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.ContactRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserService userService;

    public ContactService(ContactRepository contactRepository, UserService userService) {
        this.contactRepository = contactRepository;
        this.userService = userService;
    }

    // Crea un nuovo messaggio associato a un utente validando userId, payload, subject e message
    public Contact create(UUID userId, ContactCreateDTO dto) {
        if (userId == null) throw new ValidationException("userId is required");
        if (dto == null) throw new ValidationException("Payload is required");

        if (dto.subject() == null) throw new ValidationException("subject is required");
        if (dto.message() == null || dto.message().trim().isBlank()) {
            throw new ValidationException("message is required");
        }

        User user = userService.findById(userId);

        Contact c = new Contact();
        c.setUser(user);
        c.setSubject(dto.subject());
        c.setMessage(dto.message().trim());
        c.setCreatedAt(LocalDateTime.now());

        return contactRepository.save(c);
    }

    // Restituisce tutti i messaggi
    public List<Contact> findAll() { return contactRepository.findAll(); }

    // Recupera un messaggio tramite ID
    public Contact findById(UUID id) {
        if (id == null) throw new ValidationException("id is required");
        return contactRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    // Restituisce tutti i messaggi associati ad un utente
    public List<Contact> findByUser(UUID userId) {
        if (userId == null) throw new ValidationException("userId is required");
        return contactRepository.findByUserId(userId);
    }

    // Restituisce tutti i messaggi filtrati per subject
    public List<Contact> findBySubject(Subject subject) {
        if (subject == null) throw new ValidationException("subject is required");
        return contactRepository.findBySubject(subject);
    }

    // Restituisce i messaggi creati in un intervallo di date
    public List<Contact> findByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new ValidationException("start and end are required");
        if (end.isBefore(start)) throw new ValidationException("end must be after or equal to start");

        return contactRepository.findByCreatedAtBetween(
                start.atStartOfDay(),
                end.plusDays(1).atStartOfDay()
        );
    }
}