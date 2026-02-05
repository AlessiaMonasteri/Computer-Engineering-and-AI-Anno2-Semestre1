package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.Contact;
import AlessiaMonasteri.BagECommerce.entities.enums.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

    // Restituisce tutti i messaggi associati a uno specifico utente
    List<Contact> findByUserId(UUID userId);

    // Restituisce tutti i messaggi che hanno un determinato subject
    List<Contact> findBySubject(Subject subject);

    // Restituisce tutti i messaggi creati in un intervallo temporale
    List<Contact> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}


