package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockMovementCreateDTO(
        @NotNull UUID productId,
        @NotNull MovementType type,
        @Min(1) int quantity
) {}
