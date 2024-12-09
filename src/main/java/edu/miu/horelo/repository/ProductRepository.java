package edu.miu.horelo.repository;

import edu.miu.horelo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByEstore_EstoreId(Long estoreId);

    Optional<Product> findByProductId(Long aLong);

    Page<Product> findProductByEstore_EstoreId(Long estoreId, Pageable pageable);
}
