package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Price;

public record FoodItemResponse2(
        Long foodItemId,
        String name,
        String image,
        int calories,
        Price price,
        String description,
        EstoreResponse1 estore
) {
}
