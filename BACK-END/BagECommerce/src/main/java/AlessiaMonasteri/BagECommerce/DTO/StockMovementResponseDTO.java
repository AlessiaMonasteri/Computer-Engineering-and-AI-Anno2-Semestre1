package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.MovementType;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementResponseDTO(
        UUID id,
        UUID productId,
        String productModel,
        MovementType movementType,
        int quantity,
        LocalDateTime createdAt
) {}


