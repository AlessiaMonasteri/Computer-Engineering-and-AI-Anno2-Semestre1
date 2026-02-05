package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.StockMovementCreateDTO;
import AlessiaMonasteri.BagECommerce.DTO.StockMovementResponseDTO;
import AlessiaMonasteri.BagECommerce.entities.Product;
import AlessiaMonasteri.BagECommerce.entities.StockMovement;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.MovementType;
import AlessiaMonasteri.BagECommerce.exceptions.UnauthorizedException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                ProductService productService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
    }


    @Transactional //Esegue il metodo dentro una transazione di database
    // Crea un movimento di stock (ADD/REMOVE) in modo transazionale
    // valida input, recupera utente e prodotto, calcola stockBefore/stockAfter,
    // aggiorna stock e availability del prodotto, salva il movimento e ritorna un DTO
    public StockMovementResponseDTO create(StockMovementCreateDTO dto) {
        if (dto == null) throw new ValidationException("Payload is required");
        if (dto.productId() == null) throw new ValidationException("productId is required");
        if (dto.type() == null) throw new ValidationException("type is required");

        int qty = dto.quantity();
        if (qty <= 0) throw new ValidationException("quantity must be > 0");

        User performedBy = getCurrentUser();
        Product product = productService.findById(dto.productId());

        int stockBefore = product.getStock();
        int stockAfter;

        if (dto.type() == MovementType.ADD) {
            stockAfter = stockBefore + qty;

        } else if (dto.type() == MovementType.REMOVE) {
            if (stockBefore < qty) {
                throw new ValidationException(List.of("Not enough stock for product '" + product.getModel() + "'"));
            }
            stockAfter = stockBefore - qty;

        } else {
            throw new ValidationException(List.of("Unsupported movement type: " + dto.type()));
        }

        product.setStock(stockAfter);
        product.setAvailable(stockAfter > 0);
        productService.saveProduct(product);

        StockMovement m = new StockMovement();
        m.setPerformedBy(performedBy);
        m.setProduct(product);
        m.setMovementType(dto.type());
        m.setQuantity(qty);
        m.setCreatedAt(LocalDateTime.now());

        StockMovement saved = stockMovementRepository.save(m);

        return new StockMovementResponseDTO(
                saved.getId(),
                saved.getProduct().getId(),
                saved.getProduct().getModel(),
                saved.getMovementType(),
                saved.getQuantity(),
                saved.getCreatedAt()
        );
    }

    // Restituisce la lista dei movimenti di stock di un prodotto convertendo ogni movimento in StockMovementResponseDTO
    public List<StockMovementResponseDTO> findByProduct(UUID productId) {
        if (productId == null) throw new ValidationException("productId is required");

        List<StockMovement> list =
                stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId);

        return list.stream()
                .map(m -> new StockMovementResponseDTO(
                        m.getId(),
                        m.getProduct().getId(),
                        m.getProduct().getModel(),
                        m.getMovementType(),
                        m.getQuantity(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    // Recupera l’utente autenticato dal SecurityContext e verifica che esista una Authentication valida
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) return user;

        throw new UnauthorizedException("Invalid authentication principal");
    }

    // Restituisce tutti i movimenti di stock filtrati per tipo
    public List<StockMovement> findByMovementType(MovementType type) {
        if (type == null) throw new ValidationException("type is required");
        return stockMovementRepository.findByMovementType(type);
    }

    // Restituisce i movimenti di stock in un intervallo di date
    public List<StockMovement> findByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new ValidationException(List.of("start and end dates are required"));
        if (end.isBefore(start)) throw new ValidationException(List.of("end must be after or equal to start"));

        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay(); // end inclusive
        return stockMovementRepository.findByCreatedAtBetween(from, to);
    }

    // Calcola la quantità totale movimentata per un prodotto e un tipo di movimento
    public long sumQuantityByProductAndType(UUID productId, MovementType type) {
        Long sum = stockMovementRepository.sumQuantityByProductAndType(productId, type);
        return sum == null ? 0L : sum;
    }

    // Restituisce tutti i movimenti di stock effettuati dagli ADMIN
    public List<StockMovement> findAllMovementsByAdmins() {
        return stockMovementRepository.findAllPerformedByAdminsOrderByCreatedAtDesc();
    }
}