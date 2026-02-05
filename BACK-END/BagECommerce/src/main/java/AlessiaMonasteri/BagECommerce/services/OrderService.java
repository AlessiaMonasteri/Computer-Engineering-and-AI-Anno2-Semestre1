package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.OrderCreateDTO;
import AlessiaMonasteri.BagECommerce.DTO.OrderItemDTO;
import AlessiaMonasteri.BagECommerce.DTO.OrderResponseDTO;
import AlessiaMonasteri.BagECommerce.entities.Order;
import AlessiaMonasteri.BagECommerce.entities.OrderItem;
import AlessiaMonasteri.BagECommerce.entities.Product;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Status;
import AlessiaMonasteri.BagECommerce.exceptions.NotFoundException;
import AlessiaMonasteri.BagECommerce.exceptions.UnauthorizedException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.OrderItemRepository;
import AlessiaMonasteri.BagECommerce.repositories.OrderRepository;
import AlessiaMonasteri.BagECommerce.tools.MailgunSender;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final MailgunSender mailgunSender;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductService productService,
                        MailgunSender mailgunSender) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.mailgunSender = mailgunSender;
    }

    // Crea un nuovo ordine in modo transazionale
    // valida il payload, accorpa prodotti duplicati, controlla disponibilità/stock,
    // scala lo stock dei prodotti, salva ordine, calcola il totale,
    // invia l’email di conferma e ritorna l’OrderResponseDTO
    @Transactional
    public OrderResponseDTO create(OrderCreateDTO dto) {
        User user = getCurrentUser();

        if (dto == null) throw new ValidationException("Payload is required");
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new ValidationException(List.of("Order items are required"));
        }

        // Accorpa i prodotti duplicati
        Map<UUID, Integer> merged = new LinkedHashMap<>();
        for (OrderItemDTO item : dto.items()) {
            if (item == null) throw new ValidationException(List.of("Order item cannot be null"));
            if (item.productId() == null) throw new ValidationException(List.of("productId is required"));

            merged.merge(item.productId(), item.quantity(), Integer::sum);
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.CREATED);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> savedItems = new ArrayList<>();

        for (Map.Entry<UUID, Integer> e : merged.entrySet()) {
            UUID productId = e.getKey();
            int quantity = e.getValue();

            Product p = productService.findById(productId);

            if (!p.isAvailable()) {
                throw new ValidationException(List.of("Product '" + p.getModel() + "' is not available"));
            }

            if (p.getStock() < quantity) {
                throw new ValidationException(List.of("Not enough stock for product '" + p.getModel() + "'"));
            }

            double unitPrice = p.getUnitPrice();

            int newStock = p.getStock() - quantity;
            p.setStock(newStock);
            p.setAvailable(newStock > 0);
            productService.saveProduct(p);

            OrderItem oi = new OrderItem(p, savedOrder, quantity, unitPrice);
            savedItems.add(orderItemRepository.save(oi));
        }

        double total = savedItems.stream()
                .mapToDouble(oi -> oi.getUnitPrice() * oi.getQuantity())
                .sum();

        savedOrder.setTotal(total);
        orderRepository.save(savedOrder);

        try {
            mailgunSender.sendOrderEmail(user, savedOrder, savedItems, total);
        } catch (Exception ex) {
            System.out.println("Order email not sent: " + ex.getMessage());
        }

        return toResponse(savedOrder, savedItems);
    }

    // Recupera un ordine tramite ID
    public OrderResponseDTO findById(UUID orderId) {
        if (orderId == null) throw new ValidationException("orderId is required");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(orderId));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return toResponse(order, items);
    }

    // Recupera tutti gli ordini dell’utente autenticato
    public List<OrderResponseDTO> findMyOrders() {
        User user = getCurrentUser();

        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId())
                .stream()
                .map(o -> toResponse(o, orderItemRepository.findByOrderId(o.getId())))
                .toList();
    }

    // Recupera un ordine specifico dell’utente autenticato (orderId + userId)
    public OrderResponseDTO findMyOrderById(UUID orderId) {
        if (orderId == null) throw new ValidationException("orderId is required");

        User user = getCurrentUser();

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new NotFoundException(orderId));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return toResponse(order, items);
    }

    // Converte Order + lista OrderItem in OrderResponseDTO (mapper Entity -> DTO)
    private OrderResponseDTO toResponse(Order order, List<OrderItem> items) {

        for (OrderItem oi : items) {
            Product p = oi.getProduct();
            double lineTotal = oi.getUnitPrice() * oi.getQuantity();
        }

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getOrderDate()
        );
    }

    // Recupera l’utente autenticato dal SecurityContext, verificando che l’Authentication esista
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) return user;

        throw new UnauthorizedException("Invalid authentication principal");
    }

    // Recupera tutti gli ordini ordinati dal più recente
    public List<OrderResponseDTO> findAllOrdersDesc() {
        return orderRepository.findAllByOrderByOrderDateDesc()
                .stream()
                .map(o -> toResponse(o, orderItemRepository.findByOrderId(o.getId())))
                .toList();
    }

    // Ordini in un range di date
    public List<OrderResponseDTO> findOrdersBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException(List.of("start and end are required"));
        }
        if (end.isBefore(start)) {
            throw new ValidationException(List.of("end must be after or equal to start"));
        }

        return orderRepository.findByOrderDateBetween(start, end)
                .stream()
                .map(o -> toResponse(o, orderItemRepository.findByOrderId(o.getId())))
                .toList();
    }

    // Conteggio degli ordini di uno specifico mese e anno
    public long countOrdersByYearAndMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new ValidationException(List.of("month must be between 1 and 12"));
        }
        return orderRepository.countOrdersByYearAndMonth(year, month);
    }
}