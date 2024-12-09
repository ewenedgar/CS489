package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.ClientOrderRequest;
import edu.miu.horelo.dto.response.ClientOrderResponse;

import java.util.List;
import java.util.Optional;

public interface ClientOrderService {
    ClientOrderResponse createClientOrder(ClientOrderRequest clientOrderRequest);
    ClientOrderResponse updateClientOrder(Long id, ClientOrderRequest clientOrderRequest);
    Optional<ClientOrderResponse> getClientOrderById(Long id);
    List<ClientOrderResponse> getAllClientOrders();
    void deleteClientOrder(Long id);
}
