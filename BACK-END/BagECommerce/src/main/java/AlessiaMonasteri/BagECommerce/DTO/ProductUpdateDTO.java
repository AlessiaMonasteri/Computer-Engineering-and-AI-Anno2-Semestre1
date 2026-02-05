package AlessiaMonasteri.BagECommerce.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductUpdateDTO(
        String model,
        String collection,
        @Min(1900) Integer year,
        @PositiveOrZero Double unitPrice,
        String productImageURL,
        @Min(0) Integer stock,
        Boolean available
) {}

