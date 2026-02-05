package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.*;
import jakarta.validation.constraints.*;

public record AccessoryProductCreateDTO(
        // campi Product
        @NotBlank String model,
        @NotBlank String collection,
        @Min(1900) int year,
        @PositiveOrZero double unitPrice,
        String productImageURL,
        @Min(0) int stock,
        boolean available,

        // discriminante
        @NotNull AccessoryType accessoryType,

        // CHARM
        String charmModel,
        String charmColor,

        // HANDLES
        HandleLength handleLength,
        HandleType handleType,
        HandleRing handleRing,
        String handleColor
) {
    // validazione condizionale
    @AssertTrue(message = "If accessoryType=CHARM you must provide charmModel and charmColor. If accessoryType=HANDLES you must provide handleLength, handleType and handleColor.")
    public boolean isValidByType() {

        String cm = normalize(charmModel);
        String cc = normalize(charmColor);
        String hc = normalize(handleColor);

        if (accessoryType == AccessoryType.CHARMS) {
            return cm != null && cc != null;
        }

        if (accessoryType == AccessoryType.HANDLES) {
            return handleLength != null && handleType != null && hc != null;
        }

        return false;
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
