package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.StockMovement;
import AlessiaMonasteri.BagECommerce.entities.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    // Restituisce i movimenti di stock relativi a un prodotto ordinati per data di creazione decrescente
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(UUID productId);

    // Restituisce tutti i movimenti di stock per tipo (ADD o REMOVE)
    List<StockMovement> findByMovementType(MovementType movementType);

    // Restituisce tutti i movimenti di stock avvenuti in un intervallo temporale
    List<StockMovement> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Calcola la quantità totale movimentata per un prodotto specifico e per un tipo di movimento
    @Query("""
            SELECT SUM(sm.quantity)
            FROM StockMovement sm
            WHERE sm.product.id = :productId
              AND sm.movementType = :type
            """)
    Long sumQuantityByProductAndType(
            @Param("productId") UUID productId,
            @Param("type") MovementType type);

    // Restituisce tutti i movimenti di stock effettuati da utenti ADMIN ordinati per data di creazione decrescente
    @Query("""
            SELECT sm
            FROM StockMovement sm
            WHERE sm.performedBy.role = AlessiaMonasteri.BagECommerce.entities.enums.Role.ADMIN
            ORDER BY sm.createdAt DESC
            """)
    List<StockMovement> findAllPerformedByAdminsOrderByCreatedAtDesc();
}
