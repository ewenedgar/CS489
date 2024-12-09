package edu.miu.horelo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Allergy {
    @Id@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer allergyId;
    @NotBlank(message="name cannot be empty")
    private String name;
    @NotBlank(message="description cannot be empty")
    private String description;
    private int scale;
    // Many users can have many allergies
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
