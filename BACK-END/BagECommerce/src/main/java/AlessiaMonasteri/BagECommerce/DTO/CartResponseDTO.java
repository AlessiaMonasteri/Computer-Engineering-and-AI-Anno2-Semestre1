package AlessiaMonasteri.BagECommerce.DTO;

import java.util.List;
import java.util.UUID;

public record CartResponseDTO(
        UUID userId,
        List<CartLineDTO> items,
        double total
) {}
