package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name="food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long foodItemId;
    private String name;
    private String cuisine_type;
    private String spice_level;
    private String days_available;
    private String ingredients;
    private int calories;
    @Embedded
    private CombinedIngredients combinedIngredients;
    @Embedded
    @JsonIgnore
    private AvailableDaysAndHours availableDaysAndHours;
    private String image;
    @Embedded
    private Price price;
    private String description;
    @ManyToOne
    @JoinColumn(name="sub_category_id")
    private SubCategory subCategory;
    private String visibility;
    private LocalDateTime localDateTime;
    private LocalDateTime creationLocalDateTime;
    @Column(insertable=false, updatable=false)
    private Long estoreId;
    @Column(insertable=false, updatable=false)
    private Integer userId;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoreId") // Maps to the owning Order entity
    private Estore estore;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId") // Maps to the owning Order entity
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
