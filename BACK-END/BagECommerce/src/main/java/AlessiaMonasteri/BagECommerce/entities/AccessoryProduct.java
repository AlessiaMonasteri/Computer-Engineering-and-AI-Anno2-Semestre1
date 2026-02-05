package AlessiaMonasteri.BagECommerce.entities;

import AlessiaMonasteri.BagECommerce.entities.enums.AccessoryType;
import AlessiaMonasteri.BagECommerce.entities.enums.HandleLength;
import AlessiaMonasteri.BagECommerce.entities.enums.HandleRing;
import AlessiaMonasteri.BagECommerce.entities.enums.HandleType;
import jakarta.persistence.*;

@Entity
@Table(name = "accessory_product")
public class AccessoryProduct extends Product {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessoryType accessoryType;


    // CHARM fields
    @Column
    private String charmModel;

    @Column
    private String charmColor;

    // HANDLES fields
    @Enumerated(EnumType.STRING)
    @Column
    private HandleLength handleLength;

    @Enumerated(EnumType.STRING)
    @Column
    private HandleType handleType;

    @Enumerated(EnumType.STRING)
    private HandleRing handleRing;

    @Column
    private String handleColor;

    public AccessoryProduct() {}

    public AccessoryProduct(String model, String collection, int year, double unitPrice, String productImageURL, int stock, boolean available, AccessoryType accessoryType, String charmModel, String charmColor, HandleLength handleLength, HandleType handleType, HandleRing handleRing, String handleColor) {
        super(model, collection, year, unitPrice, productImageURL, stock, available);
        this.accessoryType = accessoryType;
        this.charmModel = charmModel;
        this.charmColor = charmColor;
        this.handleLength = handleLength;
        this.handleType = handleType;
        this.handleRing = handleRing;
        this.handleColor = handleColor;
    }

    public AccessoryType getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(AccessoryType accessoryType) {
        this.accessoryType = accessoryType;
    }

    public String getCharmModel() {
        return charmModel;
    }

    public void setCharmModel(String charmModel) {
        this.charmModel = charmModel;
    }

    public String getCharmColor() {
        return charmColor;
    }

    public void setCharmColor(String charmColor) {
        this.charmColor = charmColor;
    }

    public HandleLength getHandleLength() {
        return handleLength;
    }

    public void setHandleLength(HandleLength handleLength) {
        this.handleLength = handleLength;
    }

    public HandleType getHandleType() {
        return handleType;
    }

    public void setHandleType(HandleType handleType) {
        this.handleType = handleType;
    }

    public HandleRing getHandleRing() {
        return handleRing;
    }

    public void setHandleRing(HandleRing handleRing) {
        this.handleRing = handleRing;
    }

    public String getHandleColor() {
        return handleColor;
    }

    public void setHandleColor(String handleColor) {
        this.handleColor = handleColor;
    }

    @Override
    public String toString() {
        return "AccessoryProduct{" +
                ", accessoryType=" + accessoryType +
                ", charmModel='" + charmModel + '\'' +
                ", charmColor='" + charmColor + '\'' +
                ", handleLength=" + handleLength +
                ", handleType=" + handleType +
                ", handleRing=" + handleRing +
                ", handleColor='" + handleColor + '\'' +
                '}';
    }
}
