package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.OrderItemRequest;
import edu.miu.horelo.dto.response.OrderItemResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    OrderItemResponse createOrderItem(OrderItemRequest orderItem);

    Optional<OrderItemResponse> getOrderItemById(Long id);

    OrderItemResponse updateOrderItem(Long id, OrderItemRequest updatedOrderItem);

    void deleteOrderItem(Long id);

    List<OrderItemResponse> getAllOrderItems();

    List<OrderItemResponse> getOrderItemsByEstoreOrderId(Long estoreOrderId);

    BigDecimal calculateTotalPrice(Long estoreOrderId);
}
