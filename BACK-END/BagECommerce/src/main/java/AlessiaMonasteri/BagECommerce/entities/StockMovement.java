package AlessiaMonasteri.BagECommerce.entities;

import AlessiaMonasteri.BagECommerce.entities.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User performedBy;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Column (nullable = false)
    @Min(1)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public StockMovement() {}

    public StockMovement(int quantity, MovementType movementType, Product product, User performedBy) {
        this.quantity = quantity;
        this.movementType = movementType;
        this.product = product;
        this.performedBy = performedBy;
    }

    public UUID getId() {
        return id;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", performedBy=" + performedBy +
                ", product=" + product +
                ", movementType=" + movementType +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                '}';
    }
}
