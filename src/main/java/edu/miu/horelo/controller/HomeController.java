package edu.miu.horelo.controller;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.response.*;
import edu.miu.horelo.service.EstoreService;
import edu.miu.horelo.service.FoodItemService;
import edu.miu.horelo.service.ProductService;
import edu.miu.horelo.service.SpecialEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.ReadOnlyFileSystemException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {
    private final FoodItemService foodItemService;
    private final ProductService productService;
    private final EstoreService estoreService;
    private final SpecialEventService specialEventService;

    /**
     * Search food items by name, estore name, city, street, state, or product name.
     * Supports pagination.
     */
    @GetMapping("/search/food-items")
    public ResponseEntity<Page<FoodItemResponse>> searchFoodItems(
            @RequestParam(required = false) String foodItemName,
            @RequestParam(required = false) String estoreName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FoodItemResponse> foodItems = foodItemService.searchFoodItems(
                foodItemName, estoreName, city, street, state, productName, page, size);
        return ResponseEntity.ok(foodItems);
    }

    /**
     * Search estores by area code, street, city, or state.
     * Supports pagination.
     */
    @GetMapping("/search/estore")
    public ResponseEntity<Page<EstoreResponse>> searchEstores(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EstoreResponse> estores = estoreService.searchEstores(areaCode, street, city, state, page, size);
        return ResponseEntity.ok(estores);
    }

    /**
     * Retrieve only products, food items, or special events by estore ID.
     * Supports pagination.
     */
    @GetMapping("/estore/{estoreId}/food-items")
    public ResponseEntity<Page<FoodItemResponse>> getEstoreFoodItems(
            @PathVariable Long estoreId,
            @RequestParam(required = false) String itemType, // "product", "foodItem", "specialEvent"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FoodItemResponse> items = foodItemService.getEstoreFoodItems(estoreId, itemType, page, size);
        return ResponseEntity.ok(items);
    }
    @GetMapping("/estore/{estoreId}/product-items")
    public ResponseEntity<Page<ProductResponse>> getEstoreProductItems(
            @PathVariable Long estoreId,
            @RequestParam(required = false) String itemType, // "product", "foodItem", "specialEvent"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> items = productService.getEstoreProducts(estoreId, itemType, page, size);
        return ResponseEntity.ok(items);
    }
    @GetMapping("/estore/{estoreId}/special-items")
    public ResponseEntity<Page<SpecialEventResponse>> getEstoreSpecialEventItems(
            @PathVariable Long estoreId,
            @RequestParam(required = false) String itemType, // "product", "foodItem", "specialEvent"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SpecialEventResponse> items = specialEventService.getEstoreSpecialEvents(estoreId, itemType, page, size);
        return ResponseEntity.ok(items);
    }
    /**
     * Retrieve all food items without an associated estore.
     * Supports pagination.
     */
    @GetMapping("/food-items/no-estore")
    public ResponseEntity<Page<FoodItemResponse2>> getFoodItemsWithoutEstore(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FoodItemResponse2> foodItems = foodItemService.getFoodItemsWithoutEstore(page, size);
        return ResponseEntity.ok(foodItems);
    }

    /**
     * Retrieve all products without an associated estore.
     * Supports pagination.
     */
    @GetMapping("/products/no-estore")
    public ResponseEntity<Page<ProductResponse>> getProductsWithoutEstore(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductResponse> products = productService.getProductsWithoutEstore(page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieve all special events without an associated estore.
     * Supports pagination.
     */
    @GetMapping("/special-events/no-estore")
    public ResponseEntity<Page<SpecialEventResponse>> getSpecialEventsWithoutEstore(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SpecialEventResponse> specialEvents = specialEventService.getSpecialEventsWithoutEstore(page, size);
        return ResponseEntity.ok(specialEvents);
    }
    @GetMapping("/food-items/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemsWithId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        FoodItemResponse foodItems = foodItemService.getFoodItemById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Menu Item not found")
        );
        return ResponseEntity.ok(foodItems);
    }

    /**
     * Retrieve all products without an associated estore.
     * Supports pagination.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductsWithoutEstore(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ProductResponse products = productService.getProductById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Product Item not found")
        );
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieve all special events without an associated estore.
     * Supports pagination.
     */
    @GetMapping("/special-events/{id}")
    public ResponseEntity<SpecialEventResponse> getSpecialEventsWithoutEstore(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SpecialEventResponse specialEvents = specialEventService.getSpecialEventById(id);
        return ResponseEntity.ok(specialEvents);
    }

}
