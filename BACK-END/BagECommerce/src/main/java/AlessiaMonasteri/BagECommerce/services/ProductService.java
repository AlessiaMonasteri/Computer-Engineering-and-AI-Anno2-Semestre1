package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.ProductUpdateDTO;
import AlessiaMonasteri.BagECommerce.entities.Product;
import AlessiaMonasteri.BagECommerce.exceptions.ConflictException;
import AlessiaMonasteri.BagECommerce.exceptions.NotFoundException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    // Restituisce la lista di tutti i prodotti
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // Recupera un prodotto tramite ID
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    // Recupera un prodotto tramite model (case-insensitive)
    public Product findByModel(String model) {
        if (model == null || model.isBlank()) {
            throw new ValidationException("model is required");
        }

        return productRepository.findByModelIgnoreCase(model.trim())
                .orElseThrow(() ->
                        new NotFoundException("Product with model '" + model + "' not found")
                );
    }

    // Aggiorna un prodotto esistente: valida payload, applica solo i campi non null, verifica unicità del model,
    // valida collection/year/price/url/stock e aggiorna available in base allo stock
    public Product update(UUID id, ProductUpdateDTO dto) {
        if (dto == null) throw new ValidationException("Payload is required");

        Product p = findById(id);

        if (dto.model() != null) {
            String newModel = dto.model().trim();

            if (newModel.isBlank()) {
                throw new ValidationException("Model cannot be blank");
            }

            if (!newModel.equalsIgnoreCase(p.getModel())
                    && productRepository.existsByModelIgnoreCase(newModel)) {
                throw new ConflictException("Product with model '" + newModel + "' already exists");
            }

            p.setModel(newModel);
        }

        if (dto.collection() != null) {
            String newCollection = dto.collection().trim();
            if (newCollection.isBlank()) {
                throw new ValidationException("Collection cannot be blank");
            }
            p.setCollection(newCollection);
        }

        if (dto.year() != null) {
            int y = dto.year();
            if (y < 1900 || y > 2100) {
                throw new ValidationException("Year must be between 1900 and 2100");
            }
            p.setYear(y);
        }

        if (dto.unitPrice() != null) {
            double price = dto.unitPrice();
            if (price < 0) {
                throw new ValidationException("Unit price cannot be negative");
            }
            p.setUnitPrice(price);
        }

        if (dto.productImageURL() != null) {
            String url = dto.productImageURL().trim();
            if (url.isBlank()) {
                throw new ValidationException("Product image URL cannot be blank");
            }
            p.setProductImageURL(url);
        }

        if (dto.stock() != null) {
            int stock = dto.stock();
            if (stock < 0) {
                throw new ValidationException("Stock cannot be negative");
            }
            p.setStock(stock);
            p.setAvailable(stock > 0);
        }

        return productRepository.save(p);
    }


    // Elimina un prodotto tramite ID
    public void delete(UUID id) {
        Product p = findById(id);
        productRepository.delete(p);
    }

    // Verifica che non esista già un prodotto con lo stesso model
    public void ensureModelUnique(String model) {
        if (productRepository.existsByModelIgnoreCase(model)) {
            throw new ConflictException("Product with model '" + model + "' already exists");
        }
    }

    // Salva un prodotto
    public Product saveProduct(Product p) {
        return productRepository.save(p);
    }

    // Esegue una ricerca avanzata con filtri opzionali su model, collezione, anno, disponibilità e range prezzo
    public List<Product> search(String model,
                                String collection,
                                Integer year,
                                Boolean available,
                                Double minPrice,
                                Double maxPrice) {

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new ValidationException("minPrice cannot be greater than maxPrice");
        }

        return productRepository.search(model, collection, year, available, minPrice, maxPrice);
    }

    // Restituisce tutti i prodotti disponibili (available = true)
    public List<Product> findAvailable() {
        return productRepository.findByAvailableTrue();
    }

    // Restituisce i prodotti di una specifica collezione
    public List<Product> findByCollection(String collection) {
        if (collection == null || collection.isBlank()) {
            throw new ValidationException("collection is required");
        }
        return productRepository.findByCollection(collection.trim());
    }

    // Restituisce i prodotti di base all'anno
    public List<Product> findByYear(int year) {
        return productRepository.findByYear(year);
    }

    // Restituisce i prodotti con stock minore o uguale a una soglia
    public List<Product> findLowStock(int threshold) {
        return productRepository.findByStockLessThanEqual(threshold);
    }
}