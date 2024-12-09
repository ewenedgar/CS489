package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.FoodItem;
import edu.miu.horelo.model.Product;
import edu.miu.horelo.model.User;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String status,

        String deliveryType, //in-house // pick up
        String specialRequests,
        Long paymentId,
        BigDecimal totalPrice,
        BigDecimal discount,
        List<FoodItem> foodItemList,
        List<Product> productList,
        User creator
) {
}
