package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.AccessoryProductCreateDTO;
import AlessiaMonasteri.BagECommerce.entities.AccessoryProduct;
import AlessiaMonasteri.BagECommerce.entities.enums.AccessoryType;
import AlessiaMonasteri.BagECommerce.services.AccessoryProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products/accessories")
public class AccessoryProductController {

    private final AccessoryProductService accessoryProductService;

    public AccessoryProductController(AccessoryProductService accessoryProductService) {
        this.accessoryProductService = accessoryProductService;
    }

    // USER + ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/{id}")
    public AccessoryProduct getById(@PathVariable UUID id) {
        return accessoryProductService.findById(id);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping
    public List<AccessoryProduct> findAll() {
        return accessoryProductService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/available")
    public List<AccessoryProduct> available() {
        return accessoryProductService.findAvailableTrue();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-type")
    public List<AccessoryProduct> byType(@RequestParam AccessoryType accessoryType) {
        return accessoryProductService.findByAccessoryType(accessoryType);
    }

    // ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<AccessoryProduct> create(@RequestBody @Valid AccessoryProductCreateDTO dto) {
        AccessoryProduct saved = accessoryProductService.create(dto);
        return ResponseEntity
                .created(URI.create("/products/accessories/" + saved.getId()))
                .body(saved);
    }

    // SUPERADMIN

    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/sold")
    public long soldByType(@RequestParam AccessoryType accessoryType) {
        return accessoryProductService.countSoldByType(accessoryType);
    }

    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/revenue")
    public long revenueByType(@RequestParam AccessoryType accessoryType) {
        return accessoryProductService.sumRevenueByType(accessoryType);
    }
}