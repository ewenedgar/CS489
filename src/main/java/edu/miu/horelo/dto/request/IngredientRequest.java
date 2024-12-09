package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.Price;

public record IngredientRequest(

        String name,
        String description,
        String category,

        Price price,
        Long estoreId
) {
}
