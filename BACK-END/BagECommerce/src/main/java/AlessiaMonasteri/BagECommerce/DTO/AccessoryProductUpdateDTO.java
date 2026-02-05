package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.*;
import jakarta.validation.constraints.AssertTrue;

public record AccessoryProductUpdateDTO(
        AccessoryType accessoryType,

        String charmModel,
        String charmColor,

        HandleLength handleLength,
        HandleType handleType,
        HandleRing handleRing,
        String handleColor
) {
    @AssertTrue(message = "If accessoryType=CHARMS you must provide charmModel and charmColor. If accessoryType=HANDLES you must provide handleLength, handleType and handleColor.")
    public boolean isValidByType() {
        if (accessoryType == null) return true;

        String cm = norm(charmModel);
        String cc = norm(charmColor);
        String hc = norm(handleColor);

        if (accessoryType == AccessoryType.CHARMS) {
            return cm != null && cc != null;
        }

        if (accessoryType == AccessoryType.HANDLES) {
            return handleLength != null && handleType != null && hc != null;
        }

        return false;
    }

    private static String norm(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
