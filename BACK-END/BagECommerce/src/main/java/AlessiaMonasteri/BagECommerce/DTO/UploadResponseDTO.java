package AlessiaMonasteri.BagECommerce.DTO;

import java.time.LocalDateTime;

public record UploadResponseDTO(
        String url,
        String publicId,
        LocalDateTime timestamp
) {}

