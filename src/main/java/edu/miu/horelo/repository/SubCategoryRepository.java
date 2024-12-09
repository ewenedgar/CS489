package edu.miu.horelo.repository;

import edu.miu.horelo.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findBySubCategoryId(Long id);

    List<SubCategory> findSubCategoryByCategory_CategoryId(Long id);
}
