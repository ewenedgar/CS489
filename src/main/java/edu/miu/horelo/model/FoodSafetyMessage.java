package edu.miu.horelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "food_safety_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodSafetyMessage {
    @Id@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long foodSafetyMessageId;
    private String message;
    @OneToOne(fetch = FetchType.LAZY)
    private Estore estore;
}
