package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.ClientOrderRequest;
import edu.miu.horelo.dto.response.ClientOrderResponse;
import edu.miu.horelo.model.ClientOrder;
import edu.miu.horelo.repository.ClientOrderRepository;
import edu.miu.horelo.service.ClientOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {
    private final ClientOrderRepository clientOrderRepository;


    @Override
    public ClientOrderResponse createClientOrder(ClientOrderRequest clientOrderRequest) {
        ClientOrder clientOrder = new ClientOrder(
                null,
                clientOrderRequest.user(),
                clientOrderRequest.estoreOrders(),
                clientOrderRequest.status(),
                clientOrderRequest.clientOrderIdReference()
        );
        clientOrder = clientOrderRepository.save(clientOrder);
        return mapToClientOrderResponse(clientOrder);
    }

    @Override
    public ClientOrderResponse updateClientOrder(Long id, ClientOrderRequest clientOrderRequest) {
        ClientOrder clientOrder = clientOrderRepository.findByClientOrderId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientOrder with id " + id + " not found"));

        clientOrder.setUser(clientOrderRequest.user());
        clientOrder.setEstoreOrders(clientOrderRequest.estoreOrders());
        clientOrder.setStatus(clientOrderRequest.status());
        clientOrder.setClientOrderIdReference(clientOrderRequest.clientOrderIdReference());

        return mapToClientOrderResponse(clientOrderRepository.save(clientOrder));
    }

    @Override
    public Optional<ClientOrderResponse> getClientOrderById(Long id) {
        return clientOrderRepository.findByClientOrderId(id).map(this::mapToClientOrderResponse);
    }

    @Override
    public List<ClientOrderResponse> getAllClientOrders() {
        return clientOrderRepository.findAll().stream()
                .map(this::mapToClientOrderResponse)
                .toList();
    }

    @Override
    public void deleteClientOrder(Long id) {
        clientOrderRepository.deleteByClientOrderId(id);
    }

    private ClientOrderResponse mapToClientOrderResponse(ClientOrder clientOrder) {
        return new ClientOrderResponse(
                clientOrder.getClientOrderId(),
                clientOrder.getUser(),
                clientOrder.getEstoreOrders(),
                clientOrder.getStatus(),
                clientOrder.getClientOrderIdReference()
        );
    }
}
