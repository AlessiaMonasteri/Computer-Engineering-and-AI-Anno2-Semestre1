package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.UploadResponseDTO;
import AlessiaMonasteri.BagECommerce.entities.Product;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MediaService {

    private final Cloudinary cloudinary;
    private final ProductService productService;

    public MediaService(Cloudinary cloudinary, ProductService productService) {
        this.cloudinary = cloudinary;
        this.productService = productService;
    }

    // Carica un file immagine su Cloudinary
    // verifica che il file esista e sia un’immagine, effettua l’upload nella cartella Cloudinary, estrae l'url e il publicId dalla risposta
    public UploadResponseDTO upload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ValidationException(List.of("File is required"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException(List.of("Only image files are allowed"));
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "bag-ecommerce/products"
                    )
            );

            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            if (url == null || publicId == null) {
                throw new IllegalStateException("Cloudinary upload failed: missing response data");
            }

            return new UploadResponseDTO(
                    url,
                    publicId,
                    LocalDateTime.now()
            );

        } catch (IOException e) {
            throw new IllegalStateException("Error reading file bytes", e);
        }
    }

    // Carica un’immagine su Cloudinary e la associa a un prodotto
    public UploadResponseDTO uploadAndAttachToProduct(UUID productId, MultipartFile file) {

        if (productId == null) {
            throw new ValidationException("productId is required");
        }

        UploadResponseDTO uploaded = upload(file);

        Product p = productService.findById(productId);
        p.setProductImageURL(uploaded.url());
        productService.saveProduct(p);

        return uploaded;
    }
}