package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.UploadResponseDTO;
import AlessiaMonasteri.BagECommerce.services.MediaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/media/admin")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @PostMapping("/products/{productId}/image")
    public UploadResponseDTO uploadProductImage(@PathVariable UUID productId,
                                                @RequestParam("file") MultipartFile file) {
        return mediaService.uploadAndAttachToProduct(productId, file);
    }
}