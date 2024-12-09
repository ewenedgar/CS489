package edu.miu.horelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name="order_items")
@Data@AllArgsConstructor@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long orderItemId;

    private boolean edited;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_item_ingredient",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Embedded
    private Price unitPrice;

    @Column(length = 50)
    private String specialMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estore_order_id")
    private EstoreOrder estoreOrder; // Now links to EstoreOrder instead of ClientOrder

    public Estore getEstore() {
        if (foodItem != null) {
            return foodItem.getEstore();
        } else if (product != null) {
            return product.getEstore();
        }
        return null; // Return null if neither foodItem nor product is set
    }
}
