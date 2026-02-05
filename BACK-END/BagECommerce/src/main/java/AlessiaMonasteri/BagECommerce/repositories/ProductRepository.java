package AlessiaMonasteri.BagECommerce.repositories;

import AlessiaMonasteri.BagECommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Restituisce i prodotti disponibili con una quantità di stock maggiore del valore minimo indicato
    List<Product> findByAvailableTrueAndStockGreaterThan(int minStock);

    // Restituisce tutti i prodotti disponibili (available = true)
    List<Product> findByAvailableTrue();

    // Restituisce tutti i prodotti di una collezione
    List<Product> findByCollection(String collection);

    // Restituisce tutti i prodotti di un determinato anno
    List<Product> findByYear(int year);

    // Restituisce un prodotto cercandolo per modello (case-insensitive)
    Optional<Product> findByModelIgnoreCase(String model);

    // Restituisce i prodotti con stock inferiore/uguale alla soglia
    List<Product> findByStockLessThanEqual(int threshold);

    // Verifica se esiste già un prodotto in base al modello (case-insensitive)
    boolean existsByModelIgnoreCase(String model);

    // Ricerca avanzata dei prodotti con filtri opzionali (modello, collezione, anno, disponibilità e range di prezzo) in ordine alfabetico
    @Query("""
    SELECT p
    FROM Product p
    WHERE (:model IS NULL OR :model = '' OR LOWER(p.model) LIKE LOWER(CONCAT('%', :model, '%')))
    AND (:collection IS NULL OR :collection = '' OR LOWER(p.collection) = LOWER(:collection))
    AND (:year IS NULL OR p.year = :year)
    AND (:available IS NULL OR p.available = :available)
    AND (:minPrice IS NULL OR p.unitPrice >= :minPrice)
    AND (:maxPrice IS NULL OR p.unitPrice <= :maxPrice)
    ORDER BY p.model ASC
    """)
    List<Product> search(@Param("model") String model,
                         @Param("collection") String collection,
                         @Param("year") Integer year,
                         @Param("available") Boolean available,
                         @Param("minPrice") Double minPrice,
                         @Param("maxPrice") Double maxPrice);
}

