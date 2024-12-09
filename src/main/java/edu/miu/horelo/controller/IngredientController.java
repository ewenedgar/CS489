package edu.miu.horelo.controller;

import edu.miu.horelo.advice.UnauthorizedException;
import edu.miu.horelo.dto.request.IngredientRequest;
import edu.miu.horelo.dto.request.IngredientRequest1;
import edu.miu.horelo.dto.response.IngredientResponse;
import edu.miu.horelo.service.IngredientService;
import edu.miu.horelo.service.UserService;
import edu.miu.horelo.service.util.JWTMgmtUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = {"/api/v1/public/auth/ingredient"})
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTMgmtUtilityService jwtMgmtUtilityService;

    // Create
    @PostMapping("")
    public ResponseEntity<IngredientResponse> createIngredient(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody IngredientRequest ingredientRequest) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            IngredientResponse ingredientResponse = ingredientService.createIngredient(ingredientRequest, userId);
            return ResponseEntity.ok(ingredientResponse);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Create
    @PostMapping("/name")
    public ResponseEntity<IngredientResponse> createIngredientWithName(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody IngredientRequest1 ingredientRequest) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            IngredientResponse ingredientResponse = ingredientService.createIngredientWithName(ingredientRequest, userId);
            return ResponseEntity.ok(ingredientResponse);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Read all
    @GetMapping("")
    public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
        List<IngredientResponse> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }
    @GetMapping("/all-public")
    public ResponseEntity<List<IngredientResponse>> getAllIngredientsByEstorePublic(Long estoreId) {
        List<IngredientResponse> ingredients = ingredientService.getIngredientsByEstore(estoreId);
        return ResponseEntity.ok(ingredients);
    }
    // Read all
    @GetMapping("/estore")
    public ResponseEntity<List<IngredientResponse>> getAllIngredientsByEstore(
            @RequestHeader("Authorization") String authorizationHeader, Long estoreId
    ) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            List<IngredientResponse> ingredients = ingredientService.getIngredientsByEstore(estoreId);
            return ResponseEntity.ok(ingredients);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> getIngredientById(
            @PathVariable Long id) {
        Optional<IngredientResponse> ingredient = ingredientService.getIngredientById(id);
        return ingredient.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PatchMapping("/{id}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id, @RequestBody IngredientRequest ingredientRequest) {

        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);
            IngredientResponse updatedIngredient = ingredientService.updateIngredient(id, ingredientRequest, userId);
            if (updatedIngredient != null) {
                return ResponseEntity.ok(updatedIngredient);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            ingredientService.deleteIngredient(id, userId);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
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
