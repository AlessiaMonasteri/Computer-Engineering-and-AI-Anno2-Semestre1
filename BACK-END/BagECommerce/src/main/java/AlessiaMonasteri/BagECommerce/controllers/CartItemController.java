package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.CartItemCreateDTO;
import AlessiaMonasteri.BagECommerce.DTO.CartResponseDTO;
import AlessiaMonasteri.BagECommerce.services.CartItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/me/cart")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    // USER / ADMIN / SUPERADMIN: possono gestire SOLO il proprio carrello (me)
    // Non esistono endpoint per leggere/modificare il carrello di altri utenti.

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping
    public CartResponseDTO getMyCart() {
        return cartItemService.getMyCart();
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @PostMapping
    public CartResponseDTO add(@RequestBody @Valid CartItemCreateDTO dto) {
        return cartItemService.addItem(dto);
    }

    @PatchMapping("/{productId}")
    public CartResponseDTO updateQty(
            @PathVariable UUID productId,
            @RequestParam int quantity
    ) {
        return cartItemService.updateQuantity(productId, quantity);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @DeleteMapping("/{productId}")
    public CartResponseDTO remove(@PathVariable UUID productId) {
        return cartItemService.removeItem(productId);
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        cartItemService.clear();
    }
}