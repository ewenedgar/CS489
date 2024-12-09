package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.OrderItemRequest;
import edu.miu.horelo.dto.response.IngredientOrderItemResponseDTO;
import edu.miu.horelo.dto.response.OrderItemResponse;
import edu.miu.horelo.model.*;
import edu.miu.horelo.repository.*;
import edu.miu.horelo.service.OrderItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private FoodItemRepository foodItemRepository;
    private EstoreOrderRepository estoreOrderRepository;
    private ProductRepository productRepository;
    private IngredientRepository ingredientRepository;


    @Override
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();

        // Set basic fields
        orderItem.setQuantity(orderItemRequest.quantity());
        orderItem.setUnitPrice(orderItemRequest.unitPrice());
        orderItem.setSpecialMessage(orderItemRequest.specialMessage());
        orderItem.setEdited(false);

        // Set FoodItem if provided
        if (orderItemRequest.foodItemId() != null) {
            FoodItem foodItem = foodItemRepository.findById(orderItemRequest.foodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem with id " + orderItemRequest.foodItemId() + " not found"));
            orderItem.setFoodItem(foodItem);
        }

        // Set Product if provided
        if (orderItemRequest.productId() != null) {
            Product product = productRepository.findById(orderItemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + orderItemRequest.productId() + " not found"));
            orderItem.setProduct(product);
        }

        // Set EstoreOrder if provided
        EstoreOrder estoreOrder = estoreOrderRepository.findById(orderItemRequest.estoreOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("EstoreOrder with id " + orderItemRequest.estoreOrderId() + " not found"));
        orderItem.setEstoreOrder(estoreOrder);

        // Set Ingredients if provided
        if (orderItemRequest.ingredients() != null && !orderItemRequest.ingredients().isEmpty()) {
            List<Ingredient> ingredients = orderItemRequest.ingredients().stream()
                    .map(ingredientId -> ingredientRepository.findByIngredientId(ingredientId.ingredientId())
                            .orElseThrow(() -> new ResourceNotFoundException("Ingredient with id " + ingredientId + " not found")))
                    .collect(Collectors.toList());
            orderItem.setIngredients(ingredients);
        }

        // Save the OrderItem entity
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Convert saved OrderItem entity to response DTO
        return mapToOrderItemResponse(savedOrderItem);
    }

    @Override
    public Optional<OrderItemResponse> getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .map(this::mapToOrderItemResponse);
    }

    // Helper method to map OrderItem to OrderItemResponse
    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        List<IngredientOrderItemResponseDTO> ingredientResponses = orderItem.getIngredients().stream()
                .map(ingredient -> new IngredientOrderItemResponseDTO(ingredient.getIngredientId(), ingredient.getName(), ingredient.getPrice()))
                .collect(Collectors.toList());

        return new OrderItemResponse(
                orderItem.getOrderItemId(),
                orderItem.isEdited(),
                orderItem.getFoodItem() != null ? orderItem.getFoodItem().getFoodItemId() : null,
                orderItem.getProduct() != null ? orderItem.getProduct().getProductId() : null,
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getSpecialMessage(),
                orderItem.getEstoreOrder() != null ? orderItem.getEstoreOrder().getEstoreOrderId() : null,
                ingredientResponses
        );
    }

    @Override
    public OrderItemResponse updateOrderItem(Long id, OrderItemRequest updatedOrderItem) {
        OrderItem existingOrderItem  = orderItemRepository.findByOrderItemId(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem with id " + id + " not found"));

        // Update fields in the existing OrderItem
        existingOrderItem.setEdited(updatedOrderItem.edited());
        existingOrderItem.setQuantity(updatedOrderItem.quantity());
        existingOrderItem.setSpecialMessage(updatedOrderItem.specialMessage());

        // Update the foodItem if provided
        if (updatedOrderItem.foodItemId() != null) {
            FoodItem foodItem = foodItemRepository.findByFoodItemId(updatedOrderItem.foodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem with id " + updatedOrderItem.foodItemId() + " not found"));
            existingOrderItem.setFoodItem(foodItem);
        }

        // Update the product if provided
        if (updatedOrderItem.productId() != null) {
            Product product = productRepository.findByProductId(updatedOrderItem.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with id " + updatedOrderItem.productId() + " not found"));
            existingOrderItem.setProduct(product);
        }

        // Update the unit price
        existingOrderItem.setUnitPrice(updatedOrderItem.unitPrice());

        // Update ingredients if provided
        if (updatedOrderItem.ingredients() != null) {
            List<Ingredient> ingredients = updatedOrderItem.ingredients().stream()
                    .map(ingredientRequest -> {
                        Long ingredientId = ingredientRequest.ingredientId();
                        return ingredientRepository.findById(ingredientId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ingredient with id " + ingredientId + " not found"));
                    })
                    .toList();
            existingOrderItem.setIngredients(ingredients);
        }

        // Save the updated OrderItem
        OrderItem orderItem = orderItemRepository.save(existingOrderItem);
        return new OrderItemResponse(
                orderItem.getOrderItemId(),
                orderItem.isEdited(),
                orderItem.getFoodItem() != null ? orderItem.getFoodItem().getFoodItemId() : null,
                orderItem.getProduct() != null ? orderItem.getProduct().getProductId() : null,
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getSpecialMessage(),
                orderItem.getEstoreOrder().getEstoreOrderId(),
                // Convert each Ingredient to IngredientResponse

                //
                orderItem.getIngredients() != null ?  orderItem.getIngredients().stream()
                        .map(ingredient -> new IngredientOrderItemResponseDTO(
                                ingredient.getIngredientId(),
                                ingredient.getName(),
                                ingredient.getPrice() // Assuming Ingredient has a Price field
                        ))
                        .toList() : null
        );
    }

    @Override
    public void deleteOrderItem(Long id) {

        orderItemRepository.deleteByOrderItemId(id);
    }

    @Override
    public List<OrderItemResponse> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        return getOrderItemResponses(orderItems);
    }

    @Override
    public List<OrderItemResponse> getOrderItemsByEstoreOrderId(Long estoreOrderId) {
         List<OrderItem> orderItems = orderItemRepository.findAllByEstoreOrder_EstoreOrderId(estoreOrderId);
        return getOrderItemResponses(orderItems);
    }

    private List<OrderItemResponse> getOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> new OrderItemResponse(
                        orderItem.getOrderItemId(),
                        orderItem.isEdited(),
                        orderItem.getFoodItem() != null ? orderItem.getFoodItem().getFoodItemId() : null,
                        orderItem.getProduct() != null ? orderItem.getProduct().getProductId() : null,
                        orderItem.getQuantity(),
                        orderItem.getUnitPrice(),
                        orderItem.getSpecialMessage(),
                        orderItem.getEstoreOrder().getEstoreOrderId(),
                        // Convert each Ingredient to IngredientResponse

                        //
                        orderItem.getIngredients() != null ?  orderItem.getIngredients().stream()
                                .map(ingredient -> new IngredientOrderItemResponseDTO(
                                        ingredient.getIngredientId(),
                                        ingredient.getName(),
                                        ingredient.getPrice() // Assuming Ingredient has a Price field
                                ))
                                .toList() : null
                ))
                .toList();
    }

    @Override
    public BigDecimal calculateTotalPrice(Long estoreOrderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByEstoreOrder_EstoreOrderId(estoreOrderId);

        return orderItems.stream()
                .map(orderItem -> {
                    // Base price calculation
                    BigDecimal itemTotal = orderItem.getUnitPrice().getBasePrice()
                            .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

                    // Calculate the total price of ingredients, if any
                    BigDecimal ingredientsTotal = orderItem.getIngredients() == null ? BigDecimal.ZERO :
                            orderItem.getIngredients().stream()
                                    .map(ingredient -> ingredient.getPrice().getBasePrice()
                                            .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Add base price and ingredients total
                    return itemTotal.add(ingredientsTotal);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
