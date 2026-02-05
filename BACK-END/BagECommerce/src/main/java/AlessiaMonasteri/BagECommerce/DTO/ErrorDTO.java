package AlessiaMonasteri.BagECommerce.DTO;

import java.time.LocalDateTime;

public record ErrorDTO(String message, LocalDateTime timestamp, Object details) {
}

