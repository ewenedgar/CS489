package edu.miu.horelo.repository;

import edu.miu.horelo.model.EstoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoreOrderRepository extends JpaRepository<EstoreOrder, Long> {
    Optional <EstoreOrder> findByEstoreOrderId(Long id);

    void deleteByEstoreOrderId(Long id);
}
