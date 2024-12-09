package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name="ingredients")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ingredient {
    @Id@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long ingredientId;
    private String name;
    private String description;
    private String category;
    @Embedded
    private Price price;
    private LocalDateTime localDateTime;
    private LocalDateTime creationLocalDateTime;

    @JsonIgnore
    @ManyToMany(mappedBy = "ingredients")
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estore_id", nullable = false)
    @JsonIgnore // Prevents circular reference during JSON serialization
    private Estore estore;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",nullable = false) // Maps to the owning Order entity
    private User user;

    @PrePersist
    protected void onCreate() {
        creationLocalDateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        creationLocalDateTime = LocalDateTime.now();
        localDateTime = LocalDateTime.now();
    }

}
