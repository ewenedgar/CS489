package edu.miu.horelo.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    private BigDecimal basePrice;
    private BigDecimal discount;
    private String currency;
    //private LocalDate effective_date;


}