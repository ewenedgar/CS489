package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    @Id@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long productId;
    private String name;
    private String description;
    private String category;
    private String image;
    private int calories;

    @Embedded
    private Price price;
    @ManyToOne
    @JoinColumn(name="sub_category_id")
    private SubCategory subCategory;
    private LocalDateTime localDateTime;
    private LocalDateTime creationLocalDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estore_id", nullable = false)
    @JsonIgnore
     // Prevents circular reference during JSON serialization
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