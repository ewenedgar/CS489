package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Price;

import java.util.List;

public record OrderItemResponse(
        Long orderItemId,
        boolean edited,
        Long foodItemId,
        Long productId,
        int quantity,
        Price unitPrice,
        String specialMessage,
        Long estoreOrderId,
        List<IngredientOrderItemResponseDTO> ingredients
) {
}
