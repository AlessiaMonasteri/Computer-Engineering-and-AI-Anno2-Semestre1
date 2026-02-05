package AlessiaMonasteri.BagECommerce.tools;

import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Role;
import AlessiaMonasteri.BagECommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.superadmin.email}")
    private String email;

    @Value("${app.seed.superadmin.password}")
    private String password;

    @Value("${app.seed.superadmin.name:Super}")
    private String name;

    @Value("${app.seed.superadmin.surname:Admin}")
    private String surname;

    public SuperAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    // Il metodo run() viene eseguito automaticamente all'avvio dell'applicazione
    public void run(String... args) {
        // Se esiste già almeno un SUPERADMIN, non succede nulla
        if (userRepository.countByRole(Role.SUPERADMIN) > 0) return;

        // Se esiste un utente con questa email, viene promosso a SUPERADMIN
        userRepository.findByEmail(email).ifPresentOrElse(existing -> {
            existing.setRole(Role.SUPERADMIN);
            userRepository.save(existing);
            System.out.println("[SEED] Promoted existing user to SUPERADMIN: " + email);
        }, () -> {
            User u = new User();
            u.setName(name);
            u.setSurname(surname);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setRole(Role.SUPERADMIN);
            u.setCreatedAt(LocalDateTime.now());
            userRepository.save(u);

            System.out.println("[SEED] Created default SUPERADMIN: " + email);
        });
    }
}

