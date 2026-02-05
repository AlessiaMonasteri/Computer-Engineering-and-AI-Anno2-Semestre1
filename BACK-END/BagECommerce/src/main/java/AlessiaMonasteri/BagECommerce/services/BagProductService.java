package AlessiaMonasteri.BagECommerce.services;

import AlessiaMonasteri.BagECommerce.DTO.BagProductCreateDTO;
import AlessiaMonasteri.BagECommerce.entities.BagProduct;
import AlessiaMonasteri.BagECommerce.entities.enums.*;
import AlessiaMonasteri.BagECommerce.exceptions.ValidationException;
import AlessiaMonasteri.BagECommerce.repositories.BagProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BagProductService {

    private final BagProductRepository bagProductRepository;
    private final ProductService productService;

    public BagProductService(BagProductRepository bagProductRepository,
                             ProductService productService) {
        this.bagProductRepository = bagProductRepository;
        this.productService = productService;
    }

    // Restituisce tutte le BagProduct presenti
    public List<BagProduct> findAll() {
        return bagProductRepository.findAll();
    }

    // Restituisce tutte le BagProduct disponibili (available = true)
    public List<BagProduct> findAvailableTrue() {
        return bagProductRepository.findByAvailableTrue();
    }

    // Restituisce le BagProduct filtrate per TypeColor
    public List<BagProduct> findByTypeColor(TypeColor typeColor) {
        if (typeColor == null) throw new ValidationException("typeColor is required");
        return bagProductRepository.findByTypeColor(typeColor);
    }

    // Restituisce le BagProduct filtrate per Dimension
    public List<BagProduct> findByDimension(Dimension dimension) {
        if (dimension == null) throw new ValidationException("dimension is required");
        return bagProductRepository.findByDimension(dimension);
    }

    // Restituisce le BagProduct filtrate per YarnType
    public List<BagProduct> findByYarnType(YarnType yarnType) {
        if (yarnType == null) throw new ValidationException("yarnType is required");
        return bagProductRepository.findByYarnType(yarnType);
    }

    // Restituisce le BagProduct filtrate per tipo di chiusura
    public List<BagProduct> findByClosure(Closure closure) {
        if (closure == null) throw new ValidationException("closure is required");
        return bagProductRepository.findByClosure(closure);
    }

    // Restituisce le BagProduct filtrate per tipo di manico
    public List<BagProduct> findByHandleType(HandleType handleType) {
        if (handleType == null) throw new ValidationException("handleType is required");
        return bagProductRepository.findByHandleType(handleType);
    }

    // Crea una nuova BagProduct:
    // valida i campi obbligatori e verifica l'unicità del model,
    // mappa i campi Product + BagProduct e imposta available in base allo stock e salva l’entità
    public BagProduct create(BagProductCreateDTO dto) {
        if (dto == null) throw new ValidationException("Payload is required");

        if (dto.model() == null || dto.model().trim().isBlank()) {
            throw new ValidationException("model is required");
        }
        if (dto.collection() == null || dto.collection().trim().isBlank()) {
            throw new ValidationException("collection is required");
        }
        if (dto.unitPrice() < 0) {
            throw new ValidationException("unitPrice cannot be negative");
        }

        productService.ensureModelUnique(dto.model());

        BagProduct bag = new BagProduct();

        // campi Product
        bag.setModel(dto.model().trim());
        bag.setCollection(dto.collection().trim());
        bag.setYear(dto.year());
        bag.setUnitPrice(dto.unitPrice());
        bag.setProductImageURL(dto.productImageURL());
        bag.setStock(dto.stock());

        bag.setAvailable(dto.stock() > 0);

        // campi BagProduct
        bag.setTypeColor(dto.typeColor());
        bag.setColor1(dto.color1());
        bag.setColor2(dto.color2());
        bag.setColor3(dto.color3());
        bag.setDimension(dto.dimension());
        bag.setLengthHeightDepth(dto.lengthHeightDepth());
        bag.setEmbroidery(dto.embroidery());
        bag.setYarnType(dto.yarnType());
        bag.setClosure(dto.closure());
        bag.setBuckle(dto.buckle());
        bag.setLining(dto.lining());
        bag.setHandleType(dto.handleType());
        bag.setHandleColor(dto.handleColor());
        bag.setHandleLength(dto.handleLength());
        bag.setHandleRing(dto.handleRing());

        return bagProductRepository.save(bag);
    }

    // Conta quante borse sono state vendute per modello
    public long countBagsSoldByModelIgnoreCase(String model) {
        if (model == null || model.isBlank()) throw new ValidationException("model is required");
        Long sum = bagProductRepository.countBagsSoldByModelIgnoreCase(model.trim());
        return sum == null ? 0 : sum;
    }

    // Calcola il ricavo generato da un dato modello
    public long sumRevenueBagsByModel(String model) {
        if (model == null || model.isBlank()) throw new ValidationException("model is required");
        Long sum = bagProductRepository.sumRevenueBagsByModel(model.trim());
        return sum == null ? 0 : sum;
    }

}