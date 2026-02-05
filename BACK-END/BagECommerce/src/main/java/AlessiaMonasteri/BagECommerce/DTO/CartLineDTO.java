package AlessiaMonasteri.BagECommerce.DTO;

import java.util.UUID;


public record CartLineDTO(
        UUID productId,
        String model,
        double unitPrice,
        int quantity,
        double lineTotal
) {}

