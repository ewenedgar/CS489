package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Price;

public record IngredientResponse(
        Long ingredientId,
        String name,
        String description,
        String category,

        Price price,
        EstoreResponse1 estore

) {
}
