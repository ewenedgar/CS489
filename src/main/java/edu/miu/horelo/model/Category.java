package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name="categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long categoryId;
    private String name;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SubCategory> subCategoryList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="estore_id")
    private Estore estore;
    public Category(String name, Estore estore){
        this.name = name;
        this.estore = estore;
    }
}
