package AlessiaMonasteri.BagECommerce.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Product {
    @Id
    @GeneratedValue
    private UUID id;

    @Column (nullable = false, unique = true)
    private String model;

    @Column(nullable = false)
    private String collection;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private double unitPrice;

    @Column
    private String productImageURL;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean available;

    public Product() {}

    public Product(String model, String collection, int year, double unitPrice, String productImageURL, int stock, boolean available) {
        this.model = model;
        this.collection = collection;
        this.year = year;
        this.unitPrice = unitPrice;
        this.productImageURL = productImageURL;
        this.stock = stock;
        this.available = available;
    }

    public UUID getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", collection='" + collection + '\'' +
                ", year=" + year +
                ", unitPrice=" + unitPrice +
                ", productImageURL='" + productImageURL + '\'' +
                ", stock=" + stock +
                ", available=" + available +
                '}';
    }
}
