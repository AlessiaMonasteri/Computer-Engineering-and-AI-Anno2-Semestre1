package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.CartItemCreateDTO;
import AlessiaMonasteri.BagECommerce.DTO.CartLineDTO;
import AlessiaMonasteri.BagECommerce.DTO.CartResponseDTO;
import AlessiaMonasteri.BagECommerce.entities.CartItem;
import AlessiaMonasteri.BagECommerce.entities.Product;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.exceptions.NotFoundException;
import AlessiaMonasteri.BagECommerce.exceptions.UnauthorizedException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.CartItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartItemService(CartItemRepository cartItemRepository,
                           ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    // Restituisce il carrello dell’utente autenticato (righe + totale)
    public CartResponseDTO getMyCart() {
        User user = getCurrentUser();
        return buildCart(user);
    }

    // Aggiunge un prodotto al carrello dell’utente autenticato
    // valida payload, controlla disponibilità/stock, crea o aggiorna la riga del carrello,
    // salva e ritorna il carrello aggiornato
    @Transactional
    public CartResponseDTO addItem(CartItemCreateDTO dto) {
        User user = getCurrentUser();

        if (dto == null) throw new ValidationException("Payload is required");
        if (dto.productId() == null) throw new ValidationException("productId is required");

        Product product = productService.findById(dto.productId());

        if (!product.isAvailable()) {
            throw new ValidationException(List.of("Product is not available"));
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseGet(() -> new CartItem(0, user, product.getUnitPrice(), product));

        int newQty = item.getQuantity() + dto.quantity();

        if (product.getStock() < newQty) {
            throw new ValidationException(List.of("Not enough stock for product '" + product.getModel() + "'"));
        }

        item.setQuantity(newQty);
        item.setUnitPrice(product.getUnitPrice());
        item.setUser(user);
        item.setProduct(product);

        cartItemRepository.save(item);

        return buildCart(user);
    }

    // Aggiorna la quantità di un prodotto nel carrello dell’utente autenticato
    // valida input, controlla disponibilità/stock, recupera la riga del carrello, aggiorna quantità/prezzo,
    // salva e ritorna il carrello aggiornato
    @Transactional
    public CartResponseDTO updateQuantity(UUID productId, int quantity) {
        User user = getCurrentUser();

        if (productId == null) throw new ValidationException("productId is required");
        if (quantity <= 0) throw new ValidationException(List.of("Quantity must be > 0"));

        Product product = productService.findById(productId);

        if (!product.isAvailable()) {
            throw new ValidationException(List.of("Product is not available"));
        }

        if (product.getStock() < quantity) {
            throw new ValidationException(List.of("Not enough stock for product '" + product.getModel() + "'"));
        }

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Cart item not found for product " + productId));

        item.setQuantity(quantity);
        item.setUnitPrice(product.getUnitPrice());

        cartItemRepository.save(item);

        return buildCart(user);
    }

    // Rimuove un prodotto specifico dal carrello dell’utente autenticato e ritorna il carrello aggiornato
    @Transactional
    public CartResponseDTO removeItem(UUID productId) {
        User user = getCurrentUser();

        if (productId == null) throw new ValidationException("productId is required");

        cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return buildCart(user);
    }

    // Svuota il carrello dell’utente autenticato
    @Transactional
    public void clear() {
        User user = getCurrentUser();
        cartItemRepository.deleteByUserId(user.getId());
    }

    // Recupera l’utente autenticato dal SecurityContext verificando che l’Authentication esista
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new UnauthorizedException("Invalid authentication principal");
    }

    // Costruisce la risposta del carrello (DTO) a partire dalle righe salvate: legge gli item dell’utente, crea le CartLineDTO e calcola il totale
    private CartResponseDTO buildCart(User user) {
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());

        List<CartLineDTO> lines = new ArrayList<>();
        double total = 0.0;

        for (CartItem ci : items) {
            Product p = ci.getProduct();
            double unit = ci.getUnitPrice();
            double lineTotal = unit * ci.getQuantity();
            total += lineTotal;

            lines.add(new CartLineDTO(
                    p.getId(),
                    p.getModel(),
                    unit,
                    ci.getQuantity(),
                    lineTotal
            ));
        }

        return new CartResponseDTO(user.getId(), lines, total);
    }
}

