package edu.miu.horelo.repository;

import edu.miu.horelo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(Long categoryId);

    void deleteByCategoryId(Long id);

    List<Category> findCategoryByEstore_EstoreId(Long id);
}
