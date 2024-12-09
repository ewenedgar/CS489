package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.EstoreOrderRequest;
import edu.miu.horelo.dto.response.EstoreOrderResponse;
import edu.miu.horelo.model.EstoreOrder;
import edu.miu.horelo.repository.EstoreOrderRepository;
import edu.miu.horelo.service.EstoreOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EstoreOrderServiceImpl implements EstoreOrderService {
    private final EstoreOrderRepository estoreOrderRepository;


    @Override
    public EstoreOrderResponse createEstoreOrder(EstoreOrderRequest estoreOrderRequest) {
        EstoreOrder estoreOrder = new EstoreOrder(
                null,
                estoreOrderRequest.clientOrder(),
                estoreOrderRequest.estore(),
                estoreOrderRequest.orderItems()
        );
        estoreOrder = estoreOrderRepository.save(estoreOrder);
        return mapToEstoreOrderResponse(estoreOrder);
    }

    @Override
    public EstoreOrderResponse updateEstoreOrder(Long id, EstoreOrderRequest estoreOrderRequest) {
        EstoreOrder estoreOrder = estoreOrderRepository.findByEstoreOrderId(id)
                .orElseThrow(() -> new ResourceNotFoundException("EstoreOrder with id " + id + " not found"));

        estoreOrder.setClientOrder(estoreOrderRequest.clientOrder());
        estoreOrder.setEstore(estoreOrderRequest.estore());
        estoreOrder.setOrderItems(estoreOrderRequest.orderItems());

        return mapToEstoreOrderResponse(estoreOrderRepository.save(estoreOrder));
    }

    @Override
    public Optional<EstoreOrderResponse> getEstoreOrderById(Long id) {
        return estoreOrderRepository.findByEstoreOrderId(id).map(this::mapToEstoreOrderResponse);
    }

    @Override
    public List<EstoreOrderResponse> getAllEstoreOrders() {
        return estoreOrderRepository.findAll().stream()
                .map(this::mapToEstoreOrderResponse)
                .toList();
    }

    @Override
    public void deleteEstoreOrder(Long id) {
        estoreOrderRepository.deleteByEstoreOrderId(id);
    }

    private EstoreOrderResponse mapToEstoreOrderResponse(EstoreOrder estoreOrder) {
        return new EstoreOrderResponse(
                estoreOrder.getEstoreOrderId(),
                estoreOrder.getClientOrder(),
                estoreOrder.getEstore(),
                estoreOrder.getOrderItems()
        );
    }
}
