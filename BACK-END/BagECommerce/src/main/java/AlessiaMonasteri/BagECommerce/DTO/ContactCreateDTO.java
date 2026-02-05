package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.Subject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactCreateDTO(
        @NotNull Subject subject,
        @NotBlank String message
) {}

