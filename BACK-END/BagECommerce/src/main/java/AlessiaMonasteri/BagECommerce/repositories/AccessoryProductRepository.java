package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.AccessoryProduct;
import AlessiaMonasteri.BagECommerce.entities.enums.AccessoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccessoryProductRepository extends JpaRepository<AccessoryProduct, UUID> {

    // AccessoryProduct che risultano disponibili (available = true)
    List<AccessoryProduct> findByAvailableTrue();

    // Restituisce tutti gli AccessoryProduct
    List<AccessoryProduct> findAll();

    // Restituisce tutti gli AccessoryProduct di uno specifico accessoryType
    List<AccessoryProduct> findByAccessoryType(AccessoryType accessoryType);

    // Calcola il numero totale di accessori venduti per AccessoryType sommando le quantità degli OrderItem
    @Query("""
    SELECT SUM(oi.quantity)
    FROM OrderItem oi
    WHERE TYPE(oi.product) = AccessoryProduct
    AND oi.product.accessoryType = :accessoryType
    """)
    Long countAccessoriesSoldByType(@Param("accessoryType") AccessoryType accessoryType);

    // Calcola il ricavo totale generato da un accessoryType sommando prezzo unitario × quantità degli OrderItem
    @Query("""
    SELECT SUM(oi.unitPrice * oi.quantity)
    FROM OrderItem oi
    WHERE TYPE(oi.product) = AccessoryProduct
    AND oi.product.accessoryType = :accessoryType
    """)
    Long sumRevenueAccessoriesByType(@Param("accessoryType") AccessoryType accessoryType);
}

