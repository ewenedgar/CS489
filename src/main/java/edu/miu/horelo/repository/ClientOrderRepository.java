package edu.miu.horelo.repository;

import edu.miu.horelo.model.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    Optional<ClientOrder> findByClientOrderId(Long id);

    void deleteByClientOrderId(Long id);
}
