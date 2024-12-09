package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.EstoreOrderRequest;
import edu.miu.horelo.dto.response.EstoreOrderResponse;

import java.util.List;
import java.util.Optional;

public interface EstoreOrderService {
    EstoreOrderResponse createEstoreOrder(EstoreOrderRequest estoreOrderRequest);
    EstoreOrderResponse updateEstoreOrder(Long id, EstoreOrderRequest estoreOrderRequest);
    Optional<EstoreOrderResponse> getEstoreOrderById(Long id);
    List<EstoreOrderResponse> getAllEstoreOrders();
    void deleteEstoreOrder(Long id);
}
