package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Restituisce un utente cercandolo tramite email se presente
    Optional<User> findByEmail(String email);

    // Verifica se esiste già un utente con una determinata email (case-insensitive)
    boolean existsByEmailIgnoreCase(String email);

    // Restituisce tutti gli utenti che hanno uno specifico ruolo
    List<User> findByRole(Role role);

    // Conta il numero di utenti per ruolo
    long countByRole(Role role);
}


