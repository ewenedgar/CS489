package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name="sub_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long subCategoryId;
    private String name;
    @JoinColumn(name = "category_id")
    @ManyToOne
    @JsonIgnore
    private Category category;
    @ManyToOne
    @JoinColumn(name="estore_id")
    private Estore estore;
    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> productList;
    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FoodItem> foodItemList;

    public SubCategory(String name, Category category, Estore estore){
        this.category= category;
        this.name = name;
        this.estore = estore;
    }
}
