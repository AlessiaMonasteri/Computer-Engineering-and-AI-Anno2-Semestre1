package AlessiaMonasteri.BagECommerce.DTO;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateDTO(
        @NotEmpty List<OrderItemDTO> items
) {}

