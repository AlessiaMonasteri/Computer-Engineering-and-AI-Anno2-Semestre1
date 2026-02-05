package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Restituisce tutti gli ordini di un utente ordinati per data decrescente
    List<Order> findByUserIdOrderByOrderDateDesc(UUID userId);

    // Restituisce un ordine specifico solo se appartiene all’utente indicato
    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);

    // Restituisce tutti gli ordini effettuati in un intervallo di tempo
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // Ordini più recenti
    List<Order> findAllByOrderByOrderDateDesc();

    // Conteggio degli ordini per mese e anno
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE YEAR(o.orderDate) = :year
        AND MONTH(o.orderDate) = :month
        """)
        long countOrdersByYearAndMonth(@Param("year") int year,
                                       @Param("month") int month);
}



