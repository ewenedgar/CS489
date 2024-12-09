package edu.miu.horelo.service.impl;

import edu.miu.horelo.dto.request.IngredientRequest;
import edu.miu.horelo.dto.request.IngredientRequest1;
import edu.miu.horelo.dto.response.EstoreResponse1;
import edu.miu.horelo.dto.response.IngredientResponse;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.Ingredient;
import edu.miu.horelo.model.User;
import edu.miu.horelo.repository.EstoreRepository;
import edu.miu.horelo.repository.IngredientRepository;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private EstoreRepository estoreRepository;
    @Autowired
    private UserRepository userRepository;

    // Create
    @Override
    public IngredientResponse createIngredient(IngredientRequest productRequest, Integer userId) {
        Ingredient product = mapToIngredient(productRequest, userId);
        Ingredient savedIngredient = ingredientRepository.save(product);
        return mapToIngredientResponse(savedIngredient);
    }
    @Override
    public IngredientResponse createIngredientWithName(IngredientRequest1 productRequest, Integer userId) {
        Ingredient product = mapToIngredient(productRequest, userId);
        Ingredient savedIngredient = ingredientRepository.save(product);
        return mapToIngredientResponse(savedIngredient);
    }

    // Read
    @Override
    public List<IngredientResponse> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::mapToIngredientResponse)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<IngredientResponse> getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .map(this::mapToIngredientResponse);
    }
    @Override
    public List<IngredientResponse> getIngredientsByEstore(Long estore) {
        return ingredientRepository.findByEstore_EstoreId(estore).stream()
                .map(this::mapToIngredientResponse)
                .collect(Collectors.toList());
    }

    // Update
    @Override
    public IngredientResponse updateIngredient(Long id, IngredientRequest productRequest, Integer userId) {

       System.out.println(id + " "+productRequest );
        if (ingredientRepository.existsById(id)) {
            Ingredient product = mapToIngredient(productRequest, userId);
            product.setIngredientId(id);
            Ingredient updatedIngredient = ingredientRepository.save(product);
            return mapToIngredientResponse(updatedIngredient);
        }
        return null;
    }

    // Delete
    @Override
    public void deleteIngredient(Long id, Integer userId) {
        ingredientRepository.deleteById(id);
    }


    // Helper methods to map between entity and DTO
    private Ingredient mapToIngredient(IngredientRequest ingredientRequest, Integer userId) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientRequest.name());
        ingredient.setDescription(ingredientRequest.description());
        ingredient.setCategory(ingredientRequest.category());
        ingredient.setPrice(ingredientRequest.price());
        // Convert EstoreResponse1 to Estore entity
        Estore estore = estoreRepository.findById(ingredientRequest.estoreId())
                .orElseThrow(() -> new EntityNotFoundException("Estore not found"));
        ingredient.setEstore(estore); // Assuming bidirectional mapping is set up
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ingredient.setUser(user);
        return ingredient;
    }
    private Ingredient mapToIngredient(IngredientRequest1 ingredientRequest, Integer userId) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientRequest.name());
        ingredient.setCategory(ingredientRequest.category());
        // Convert EstoreResponse1 to Estore entity
        Estore estore = estoreRepository.findById(ingredientRequest.estoreId())
                .orElseThrow(() -> new EntityNotFoundException("Estore not found"));
        ingredient.setEstore(estore); // Assuming bidirectional mapping is set up
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ingredient.setUser(user);
        return ingredient;
    }

    private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getIngredientId(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getCategory(),
                ingredient.getPrice(),
                new EstoreResponse1(ingredient.getEstore().getEstoreId(), ingredient.getEstore().getName())
        );
    }
}