package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.AccessoryProductCreateDTO;
import AlessiaMonasteri.BagECommerce.entities.AccessoryProduct;
import AlessiaMonasteri.BagECommerce.entities.enums.AccessoryType;
import AlessiaMonasteri.BagECommerce.exceptions.NotFoundException;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.AccessoryProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccessoryProductService {

    private final AccessoryProductRepository accessoryProductRepository;
    private final ProductService productService;

    public AccessoryProductService(AccessoryProductRepository accessoryProductRepository,
                                   ProductService productService) {
        this.accessoryProductRepository = accessoryProductRepository;
        this.productService = productService;
    }

    // Restituisce tutti gli AccessoryProduct presenti
    public List<AccessoryProduct> findAll() {
        return accessoryProductRepository.findAll();
    }

    // Recupera un AccessoryProduct tramite ID
    public AccessoryProduct findById(UUID id) {
        if (id == null) throw new ValidationException("id is required");
        return accessoryProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    // Restituisce tutti gli AccessoryProduct disponibili (available = true)
    public List<AccessoryProduct> findAvailableTrue() {
        return accessoryProductRepository.findByAvailableTrue();
    }

    // Restituisce gli AccessoryProduct filtrati per tipo
    public List<AccessoryProduct> findByAccessoryType(AccessoryType accessoryType) {
        if (accessoryType == null) throw new ValidationException("accessoryType is required");
        return accessoryProductRepository.findByAccessoryType(accessoryType);
    }

    // Crea un nuovo AccessoryProduct:
    // valida i campi Product e il tipo, verifica l'unicità del model,
    // imposta available in base allo stock e valorizza i campi specifici in base ad AccessoryType
    // (CHARMS: richiede charmModel/charmColor e azzera campi handle; HANDLES: richiede campi handle e azzera campi charm)
    public AccessoryProduct create(AccessoryProductCreateDTO dto) {
        if (dto == null) throw new ValidationException("Payload is required");

        if (dto.model() == null || dto.model().trim().isBlank()) throw new ValidationException("model is required");
        if (dto.collection() == null || dto.collection().trim().isBlank()) throw new ValidationException("collection is required");
        if (dto.unitPrice() < 0) throw new ValidationException("unitPrice cannot be negative");
        if (dto.accessoryType() == null) throw new ValidationException("accessoryType is required");

        productService.ensureModelUnique(dto.model());

        AccessoryProduct accessory = new AccessoryProduct();

        // Product fields
        accessory.setModel(dto.model().trim());
        accessory.setCollection(dto.collection().trim());
        accessory.setYear(dto.year());
        accessory.setUnitPrice(dto.unitPrice());
        accessory.setProductImageURL(dto.productImageURL());
        accessory.setStock(dto.stock());

        accessory.setAvailable(dto.stock() > 0);

        // Accessory fields
        accessory.setAccessoryType(dto.accessoryType());

        if (dto.accessoryType() == AccessoryType.CHARMS) {
            if (dto.charmModel() == null || dto.charmModel().trim().isBlank()) {
                throw new ValidationException("charmModel is required for CHARMS");
            }
            if (dto.charmColor() == null || dto.charmColor().trim().isBlank()) {
                throw new ValidationException("charmColor is required for CHARMS");
            }

            accessory.setCharmModel(dto.charmModel().trim());
            accessory.setCharmColor(dto.charmColor().trim());

            accessory.setHandleLength(null);
            accessory.setHandleType(null);
            accessory.setHandleRing(null);
            accessory.setHandleColor(null);

        } else if (dto.accessoryType() == AccessoryType.HANDLES) {
            if (dto.handleLength() == null) throw new ValidationException("handleLength is required for HANDLES");
            if (dto.handleType() == null) throw new ValidationException("handleType is required for HANDLES");
            if (dto.handleRing() == null) throw new ValidationException("handleRing is required for HANDLES");
            if (dto.handleColor() == null || dto.handleColor().trim().isBlank()) {
                throw new ValidationException("handleColor is required for HANDLES");
            }

            accessory.setHandleLength(dto.handleLength());
            accessory.setHandleType(dto.handleType());
            accessory.setHandleRing(dto.handleRing());
            accessory.setHandleColor(dto.handleColor().trim());

            accessory.setCharmModel(null);
            accessory.setCharmColor(null);

        } else {
            throw new ValidationException("Unsupported accessoryType: " + dto.accessoryType());
        }

        return accessoryProductRepository.save(accessory);
    }

    // Conta quanti accessori sono stati venduti per un determinato AccessoryType
    public long countSoldByType(AccessoryType accessoryType) {
        if (accessoryType == null) throw new ValidationException("accessoryType is required");
        Long sum = accessoryProductRepository.countAccessoriesSoldByType(accessoryType);
        return sum == null ? 0 : sum;
    }

    // Calcola il fatturato totale generato da un determinato AccessoryType
    public long sumRevenueByType(AccessoryType accessoryType) {
        if (accessoryType == null) throw new ValidationException("accessoryType is required");
        Long sum = accessoryProductRepository.sumRevenueAccessoriesByType(accessoryType);
        return sum == null ? 0 : sum;
    }
}

