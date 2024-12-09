package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.IngredientRequest;
import edu.miu.horelo.dto.request.IngredientRequest1;
import edu.miu.horelo.dto.response.IngredientResponse;

import java.util.List;
import java.util.Optional;

public interface IngredientService {
    List<IngredientResponse> getAllIngredients();
    Optional<IngredientResponse> getIngredientById(Long id);
    List<IngredientResponse> getIngredientsByEstore(Long estoreId);
    IngredientResponse updateIngredient(Long id, IngredientRequest ingredientRequest, Integer userId);
    void deleteIngredient(Long id, Integer userId);

    IngredientResponse createIngredient(IngredientRequest ingredientRequest, Integer userId);
    IngredientResponse createIngredientWithName(IngredientRequest1 ingredientRequest, Integer userId);
}
