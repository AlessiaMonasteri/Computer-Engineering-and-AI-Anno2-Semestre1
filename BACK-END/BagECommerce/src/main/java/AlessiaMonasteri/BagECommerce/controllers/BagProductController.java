package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.BagProductCreateDTO;
import AlessiaMonasteri.BagECommerce.entities.BagProduct;
import AlessiaMonasteri.BagECommerce.entities.enums.*;
import AlessiaMonasteri.BagECommerce.services.BagProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products/bags")
public class BagProductController {

    private final BagProductService bagProductService;

    public BagProductController(BagProductService bagProductService) {
        this.bagProductService = bagProductService;
    }

    // USER + ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping
    public List<BagProduct> findAll() {
        return bagProductService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/available")
    public List<BagProduct> available() {
        return bagProductService.findAvailableTrue();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-color")
    public List<BagProduct> byColor(@RequestParam TypeColor typeColor) {
        return bagProductService.findByTypeColor(typeColor);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-dimension")
    public List<BagProduct> byDimension(@RequestParam Dimension dimension) {
        return bagProductService.findByDimension(dimension);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-yarn")
    public List<BagProduct> byYarn(@RequestParam YarnType yarnType) {
        return bagProductService.findByYarnType(yarnType);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-closure")
    public List<BagProduct> byClosure(@RequestParam Closure closure) {
        return bagProductService.findByClosure(closure);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/by-handle")
    public List<BagProduct> byHandle(@RequestParam HandleType handleType) {
        return bagProductService.findByHandleType(handleType);
    }

    // ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @PostMapping("/admin/create")
    public BagProduct create(@RequestBody BagProductCreateDTO dto) {
        return bagProductService.create(dto);
    }

    // SUPERADMIN

    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/sold")
    public long soldByModel(@RequestParam String model) {
        return bagProductService.countBagsSoldByModelIgnoreCase(model);
    }

    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/revenue")
    public long revenueByModel(@RequestParam String model) {
        return bagProductService.sumRevenueBagsByModel(model);
    }
}