package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.OrderResponseDTO;
import AlessiaMonasteri.BagECommerce.entities.OrderItem;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.exceptions.AuthorizationDeniedException;
import AlessiaMonasteri.BagECommerce.exceptions.UnauthorizedException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.OrderItemRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }

    // Verifica se un utente ha ruolo ADMIN o SUPERADMIN controllando le authorities
    private boolean isAdmin(User u) {
        return u.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ADMIN") || a.equals("SUPERADMIN"));
    }

    // Controlla che l’ordine sia accessibile dall’utente corrente
    // valida l'input, recupera l’ordine e verifica se l’utente è proprietario oppure admin
    private void ensureOrderAccessible(UUID orderId, User currentUser) {
        if (orderId == null) throw new ValidationException("orderId is required");
        if (currentUser == null) throw new UnauthorizedException("Authentication required");

        OrderResponseDTO order = orderService.findById(orderId);

        boolean owner = order.userId() != null && order.userId().equals(currentUser.getId());
        if (!owner && !isAdmin(currentUser)) {
            throw new AuthorizationDeniedException("Forbidden");
        }
    }

    // Restituisce le righe di un ordine solo se l’utente corrente ha i permessi
    public List<OrderItem> findByOrderWithAccessControl(UUID orderId, User currentUser) {
        ensureOrderAccessible(orderId, currentUser);
        return orderItemRepository.findByOrderId(orderId);
    }

    // Calcola il totale di un ordine solo se l’utente corrente ha i permessi
    public double calculateOrderTotalWithAccessControl(UUID orderId, User currentUser) {
        ensureOrderAccessible(orderId, currentUser);
        Double total = orderItemRepository.calculateTotalByOrderId(orderId);
        return total == null ? 0.0 : total;
    }

    // Restituisce le righe di un ordine dato l’ID
    public List<OrderItem> findAllOrdersById(UUID orderId) {
        if (orderId == null) throw new ValidationException("orderId is required");
        return orderItemRepository.findByOrderId(orderId);
    }

    // Calcola il totale di un ordine dato l’ID
    public double calculateOrderTotalById(UUID orderId) {
        if (orderId == null) throw new ValidationException("orderId is required");
        Double total = orderItemRepository.calculateTotalByOrderId(orderId);
        return total == null ? 0.0 : total;
    }

}