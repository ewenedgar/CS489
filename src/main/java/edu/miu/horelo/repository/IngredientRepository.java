package edu.miu.horelo.repository;

import edu.miu.horelo.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByEstore_EstoreId(Long estoreId);
    List<Ingredient> findAllByIngredientId(Long id);
    Optional<Ingredient> findByIngredientId(Long id);
}
