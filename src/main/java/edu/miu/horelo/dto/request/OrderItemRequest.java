package edu.miu.horelo.dto.request;

import edu.miu.horelo.dto.response.IngredientResponse;
import edu.miu.horelo.model.Price;

import java.util.List;

public record OrderItemRequest(
        boolean edited,
        Long foodItemId,
        Long productId,
        int quantity,
        Price unitPrice,
        String specialMessage,
        Long estoreOrderId,
        List<IngredientResponse> ingredients
) {
}
