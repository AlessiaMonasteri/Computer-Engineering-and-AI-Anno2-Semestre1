package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.OrderCreateDTO;
import AlessiaMonasteri.BagECommerce.DTO.OrderResponseDTO;
import AlessiaMonasteri.BagECommerce.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // USER + ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO create(@RequestBody @Valid OrderCreateDTO dto) {
        return orderService.create(dto);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/me")
    public List<OrderResponseDTO> myOrders() {
        return orderService.findMyOrders();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/me/{orderId}")
    public OrderResponseDTO getMyOrderById(@PathVariable UUID orderId) {
        return orderService.findMyOrderById(orderId);
    }

    // ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/{orderId}")
    public OrderResponseDTO getById(@PathVariable UUID orderId) {
        return orderService.findById(orderId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/recent")
    public List<OrderResponseDTO> getAllOrdersRecent() {
        return orderService.findAllOrdersDesc();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/by-date")
    public List<OrderResponseDTO> getOrdersBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return orderService.findOrdersBetween(start, end);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/count")
    public long countOrders(@RequestParam int year,
                            @RequestParam int month) {
        return orderService.countOrdersByYearAndMonth(year, month);
    }
}