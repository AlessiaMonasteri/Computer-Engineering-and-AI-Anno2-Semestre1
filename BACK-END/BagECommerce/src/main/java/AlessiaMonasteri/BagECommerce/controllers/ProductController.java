package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.ProductUpdateDTO;
import AlessiaMonasteri.BagECommerce.entities.Product;
import AlessiaMonasteri.BagECommerce.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // USER + ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/{id}")
    public Product getById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    // filtro per modello
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/model/{model}")
    public Product getByModel(@PathVariable String model) {
        return productService.findByModel(model);
    }

    // funzionalità di search (sidebar dei filtri)
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/search")
    public List<Product> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String collection,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return productService.search(q, collection, year, available, minPrice, maxPrice);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/available")
    public List<Product> available() {
        return productService.findAvailable();
    }

    // filtro per collection
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-collection")
    public List<Product> byCollection(@RequestParam String collection) {
        return productService.findByCollection(collection);
    }

    // filtro per year
    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-year")
    public List<Product> byYear(@RequestParam int year) {
        return productService.findByYear(year);
    }

    // ADMIN + SUPERADMIN

    // prodotti sotto soglia stock
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/low-stock")
    public List<Product> lowStock(@RequestParam(defaultValue = "0") int threshold) {
        return productService.findLowStock(threshold);
    }

    // SUPERADMIN

    // aggiornamento caratteristiche prodotto
    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @PutMapping("/superadmin/{id}")
    public Product update(@PathVariable UUID id, @RequestBody @Valid ProductUpdateDTO dto) {
        return productService.update(id, dto);
    }

    // cancellazione prodotto
    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @DeleteMapping("/superadmin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        productService.delete(id);
    }


}
