package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Price;

public record IngredientOrderItemResponseDTO(

        Long ingredientId,
        String name,

        Price price


) {
}
