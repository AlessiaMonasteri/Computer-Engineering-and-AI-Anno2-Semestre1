package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.*;
import jakarta.validation.constraints.*;

public record BagProductCreateDTO(
        // campi Product
        @NotBlank String model,
        @NotBlank String collection,
        @Min(1900) int year,
        @PositiveOrZero double unitPrice,
        String productImageURL,
        @Min(0) int stock,
        boolean available,

        // campi BagProduct
        @NotNull TypeColor typeColor,
        @NotBlank String color1,
        String color2,
        String color3,
        @NotNull Dimension dimension,
        String lengthHeightDepth,
        @NotBlank String embroidery,
        @NotNull YarnType yarnType,
        @NotNull Closure closure,
        @NotNull Boolean buckle,
        @NotNull Boolean lining,
        @NotNull HandleType handleType,
        @NotBlank String handleColor,
        @NotNull HandleLength handleLength,
        HandleRing handleRing
) {}

