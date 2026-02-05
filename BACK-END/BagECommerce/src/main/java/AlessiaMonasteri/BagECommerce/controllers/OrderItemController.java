package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.entities.OrderItem;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.services.OrderItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // SELF: USER + ADMIN + SUPERADMIN (solo propri)

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/me/{orderId}")
    public List<OrderItem> getOrderItems(@PathVariable UUID orderId,
                                         @AuthenticationPrincipal User currentUser) {
        return orderItemService.findByOrderWithAccessControl(orderId, currentUser);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/me/{orderId}/total")
    public double getOrderTotal(@PathVariable UUID orderId,
                                @AuthenticationPrincipal User currentUser) {
        return orderItemService.calculateOrderTotalWithAccessControl(orderId, currentUser);
    }

    // ADMIN: ADMIN + SUPERADMIN (tutti gli ordini)

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/{orderId}")
    public List<OrderItem> findAllOrdersById(@PathVariable UUID orderId) {
        return orderItemService.findAllOrdersById(orderId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/{orderId}/total")
    public double calculateOrderTotalById(@PathVariable UUID orderId) {
        return orderItemService.calculateOrderTotalById(orderId);
    }
}
