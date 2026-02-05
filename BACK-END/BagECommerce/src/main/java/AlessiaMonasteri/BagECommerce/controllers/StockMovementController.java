package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.StockMovementCreateDTO;
import AlessiaMonasteri.BagECommerce.DTO.StockMovementResponseDTO;
import AlessiaMonasteri.BagECommerce.entities.StockMovement;
import AlessiaMonasteri.BagECommerce.entities.enums.MovementType;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final AlessiaMonasteri.BagECommerce.services.StockMovementService stockMovementService;

    public StockMovementController(AlessiaMonasteri.BagECommerce.services.StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    // ADMIN + SUPERADMIN

    // Admin registra movimento di RESTOCK
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @PostMapping("/admin")
    public StockMovementResponseDTO create(@RequestBody @Valid StockMovementCreateDTO dto) {
        return stockMovementService.create(dto);
    }

    // Admin filtra per tipo
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/by-type")
    public List<StockMovement> getByMovementType(@RequestParam MovementType type) {
        return stockMovementService.findByMovementType(type);
    }

    // Admin filtra per range date
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/by-date")
    public List<StockMovement> getByDateRange(@RequestParam LocalDate start,
                                              @RequestParam LocalDate end) {
        return stockMovementService.findByDateRange(start, end);
    }

    // SUPERADMIN
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/performed-by-admins")
    public List<StockMovement> movementsByAdmins() {
        return stockMovementService.findAllMovementsByAdmins();
    }

    // Somma per prodotto e tipo
    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/products/{productId}/sum")
    public long sumByProductAndType(@PathVariable UUID productId,
                                    @RequestParam MovementType type) {
        return stockMovementService.sumQuantityByProductAndType(productId, type);
    }

    // Storico per prodotto
    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/product/{productId}")
    public List<StockMovementResponseDTO> byProduct(@PathVariable UUID productId) {
        return stockMovementService.findByProduct(productId);
    }
}
