package edu.miu.horelo.repository;

import edu.miu.horelo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByEstoreOrder_EstoreOrderId(Long estoreOrderId);

    void deleteByOrderItemId(Long id);

    Optional<OrderItem> findByOrderItemId(Long id);
}
