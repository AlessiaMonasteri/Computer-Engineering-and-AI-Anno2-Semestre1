package AlessiaMonasteri.BagECommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.util.UUID;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {
    @Id
    @GeneratedValue
    private UUID id;

    @Column (nullable = false)
    @Min(1)
    private int quantity;

    @Column (nullable = false)
    private double unitPrice;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public CartItem() {}

    public CartItem(int quantity, User user, double unitPrice, Product product) {
        this.quantity = quantity;
        this.user = user;
        this.unitPrice = unitPrice;
        this.product = product;
    }

    public UUID getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "CartItems{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", user=" + user +
                ", product=" + product +
                '}';
    }
}
