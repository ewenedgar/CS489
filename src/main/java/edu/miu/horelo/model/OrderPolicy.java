package edu.miu.horelo.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderPolicy {
    private int defaultWaitingTime; // in minutes
    private int processingTime;      // in minutes
    private int confirmedTime;       // in minutes


    // Getters and Setters...
}