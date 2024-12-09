package edu.miu.horelo.controller;

import edu.miu.horelo.advice.UnauthorizedException;
import edu.miu.horelo.dto.request.FoodItemRequest;
import edu.miu.horelo.dto.response.FoodItemResponse;
import edu.miu.horelo.service.FoodItemService;
import edu.miu.horelo.service.UserService;
import edu.miu.horelo.service.util.JWTMgmtUtilityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = {"/api/v1/public/auth"})
//@PreAuthorize("hasRole('ROLE_SUPPORT')")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTMgmtUtilityService jwtMgmtUtilityService;

    // Create FoodItem
    @PostMapping("/foodItem")
    public ResponseEntity<FoodItemResponse> createFoodItem(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestBody FoodItemRequest foodItemRequest) {

        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            // Create the food item
            FoodItemResponse foodItemResponse = foodItemService.createFoodItem(foodItemRequest, userId);

            // Return 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(foodItemResponse);

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Update FoodItem
    @PatchMapping("/foodItem/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody FoodItemRequest foodItemRequest) {

        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            // Update the food item
            FoodItemResponse updatedFoodItem = foodItemService.updateFoodItem(id, foodItemRequest, userId);

            return ResponseEntity.ok(updatedFoodItem);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    @PatchMapping("/foodItem/{id}/logo")
    public ResponseEntity<FoodItemResponse> updateFoodItemLogo(@RequestHeader("Authorization") String token,
                                                            @PathVariable("id") Long id,  // Handle user profile data
                                                            @RequestPart(value="file", required = false) MultipartFile file // Handle file upload (optional)
    ) {

        if (file == null|| file.isEmpty()) {

            System.out.println("No file received " + id );
            return ResponseEntity.badRequest().body(null);
        } else {
            System.out.println("Received file: " + file.getOriginalFilename());
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));

        FoodItemResponse foodItemResponse = foodItemService.updateFoodItemLogo(id,  file);
        return ResponseEntity.ok(foodItemResponse);
    }
    @GetMapping("/foodItem/hello")
    public String hello() {
        return "Hello";
    }
    // Read all FoodItems
    @GetMapping("/foodItem")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> foodItems = foodItemService.getAllFoodItems();
        if(foodItems.isEmpty()) {

             return   ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseEntity.ok(foodItems);
    }

    // Read FoodItem by ID
    @GetMapping("/foodItem/{id}")
    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        Optional<FoodItemResponse> foodItem = foodItemService.getFoodItemById(id);
        return foodItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Read FoodItems by Estore
    @GetMapping("/foodItem/estore/{estoreId}")
    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByEstore(@PathVariable Long estoreId) {
        List<FoodItemResponse> foodItems = foodItemService.getFoodItemsByEstore(estoreId);
        if(foodItems == null){

             return   ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseEntity.ok(foodItems);
    }

    // Delete FoodItem
    @DeleteMapping("/foodItem/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        try {
            foodItemService.deleteFoodItem(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Helper method to extract the Bearer token
    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header missing or invalid");
        }
        return authorizationHeader.substring(7);
    }

    // Helper method to extract user ID from JWT token
    private Integer extractUserIdFromToken(String token) {
        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
        return userService.findUserIdByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
