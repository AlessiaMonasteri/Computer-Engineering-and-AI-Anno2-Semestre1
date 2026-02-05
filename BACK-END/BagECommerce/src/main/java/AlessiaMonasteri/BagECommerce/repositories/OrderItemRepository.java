package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    // Restituisce l'ordine cercandolo per orderId
    List<OrderItem> findByOrderId(UUID orderId);

    // Restituisce il totale per OrderId
    @Query("""
        SELECT SUM(oi.unitPrice * oi.quantity)
        FROM OrderItem oi
        WHERE oi.order.id = :orderId
        """)
    Double calculateTotalByOrderId(@Param("orderId") UUID orderId);
}

