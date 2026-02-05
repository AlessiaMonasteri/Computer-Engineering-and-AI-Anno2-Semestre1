package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.BagProduct;
import AlessiaMonasteri.BagECommerce.entities.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BagProductRepository extends JpaRepository<BagProduct, UUID> {

    // Restituisce tutte le BagProduct disponibili (available = true)
    List<BagProduct> findByAvailableTrue();

    // Restituisce tutte le BagProduct con una specifica tipologia di colorazione
    List<BagProduct> findByTypeColor(TypeColor typeColor);

    // Restituisce tutte le BagProduct per dimensione
    List<BagProduct> findByDimension(Dimension dimension);

    // Restituisce tutte le BagProduct realizzate con un determinato tipo di filato
    List<BagProduct> findByYarnType(YarnType yarnType);

    // Restituisce tutte le BagProduct per tipo di chiusura
    List<BagProduct> findByClosure(Closure closure);

    // Restituisce tutte le BagProduct per tipo di manico
    List<BagProduct> findByHandleType(HandleType handleType);

    // Calcola il numero di borse vendute per modello (ignora maiuscole/minuscole del modello)
    @Query("""
    SELECT SUM(oi.quantity)
    FROM OrderItem oi
    WHERE TYPE(oi.product) = BagProduct
    AND LOWER(oi.product.model) = LOWER(:model)
    """)
    Long countBagsSoldByModelIgnoreCase(@Param("model") String model);

    // Calcola il ricavo per modello di borsa sommando prezzo unitario × quantità degli OrderItem
    @Query("""
    SELECT SUM(oi.unitPrice * oi.quantity)
    FROM OrderItem oi
    WHERE TYPE(oi.product) = BagProduct
    AND oi.product.model = :model
    """)
    Long sumRevenueBagsByModel(@Param("model") String model);
}

