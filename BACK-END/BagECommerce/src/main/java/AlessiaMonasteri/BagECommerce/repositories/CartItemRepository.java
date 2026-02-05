package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    // Restituisce tutti gli articoli del carrello associati a uno specifico utente
    List<CartItem> findByUserId(UUID userId);

    // Restituisce un CartItem specifico per un utente e un prodotto, se presente nel carrello
    Optional<CartItem> findByUserIdAndProductId(UUID userId, UUID productId);

    // Elimina tutti gli articoli del carrello di un determinato utente
    void deleteByUserId(UUID userId);

    // Elimina un singolo articolo dal carrello identificato dall'utente e dal prodotto
    void deleteByUserIdAndProductId(UUID userId, UUID productId);
}
