package AlessiaMonasteri.BagECommerce.entities;

import AlessiaMonasteri.BagECommerce.entities.enums.*;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "bag_product")
public class BagProduct extends Product {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeColor typeColor;

    @Column(nullable = false)
    private String color1;

    @Column
    private String color2;

    @Column
    private String color3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dimension dimension;

    @Column
    private String lengthHeightDepth;

    @Column(nullable = false)
    private String embroidery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YarnType yarnType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Closure closure;

    @Column(nullable = false)
    private Boolean buckle;

    @Column(nullable = false)
    private Boolean lining;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HandleType handleType;

    @Column(nullable = false)
    private String handleColor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HandleLength handleLength;

    @Enumerated(EnumType.STRING)
    private HandleRing handleRing;

    public BagProduct() {}

    public BagProduct(TypeColor typeColor, String color1, String color2, String color3, Dimension dimension, String lengthHeightDepth, String embroidery, YarnType yarnType, Closure closure, Boolean buckle, Boolean lining, HandleType handleType, String handleColor, HandleLength handleLength, HandleRing handleRing) {
        this.typeColor = typeColor;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.dimension = dimension;
        this.lengthHeightDepth = lengthHeightDepth;
        this.embroidery = embroidery;
        this.yarnType = yarnType;
        this.closure = closure;
        this.buckle = buckle;
        this.lining = lining;
        this.handleType = handleType;
        this.handleColor = handleColor;
        this.handleLength = handleLength;
        this.handleRing = handleRing;
    }

    public TypeColor getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(TypeColor typeColor) {
        this.typeColor = typeColor;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getColor3() {
        return color3;
    }

    public void setColor3(String color3) {
        this.color3 = color3;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public String getLengthHeightDepth() {
        return lengthHeightDepth;
    }

    public void setLengthHeightDepth(String lengthHeightDepth) {
        this.lengthHeightDepth = lengthHeightDepth;
    }

    public String getEmbroidery() {
        return embroidery;
    }

    public void setEmbroidery(String embroidery) {
        this.embroidery = embroidery;
    }

    public YarnType getYarnType() {
        return yarnType;
    }

    public void setYarnType(YarnType yarnType) {
        this.yarnType = yarnType;
    }

    public Closure getClosure() {
        return closure;
    }

    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    public Boolean getBuckle() {
        return buckle;
    }

    public void setBuckle(Boolean buckle) {
        this.buckle = buckle;
    }

    public Boolean getLining() {
        return lining;
    }

    public void setLining(Boolean lining) {
        this.lining = lining;
    }

    public HandleType getHandleType() {
        return handleType;
    }

    public void setHandleType(HandleType handleType) {
        this.handleType = handleType;
    }

    public String getHandleColor() {
        return handleColor;
    }

    public void setHandleColor(String handleColor) {
        this.handleColor = handleColor;
    }

    public HandleLength getHandleLength() {
        return handleLength;
    }

    public void setHandleLength(HandleLength handleLength) {
        this.handleLength = handleLength;
    }

    public HandleRing getHandleRing() {
        return handleRing;
    }

    public void setHandleRing(HandleRing handleRing) {
        this.handleRing = handleRing;
    }

    @Override
    public String toString() {
        return "ProductDescription{" +
                ", typeColor=" + typeColor +
                ", color1='" + color1 + '\'' +
                ", color2='" + color2 + '\'' +
                ", color3='" + color3 + '\'' +
                ", dimension=" + dimension +
                ", lengthHeightDepth='" + lengthHeightDepth + '\'' +
                ", embroidery='" + embroidery + '\'' +
                ", yarnType=" + yarnType +
                ", closure=" + closure +
                ", buckle=" + buckle +
                ", lining=" + lining +
                ", handleType=" + handleType +
                ", handleColor='" + handleColor + '\'' +
                ", handleLength=" + handleLength +
                ", handleRing=" + handleRing +
                '}';
    }
}
