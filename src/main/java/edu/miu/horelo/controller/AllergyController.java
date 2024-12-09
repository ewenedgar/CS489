package edu.miu.horelo.controller;

import edu.miu.horelo.advice.UnauthorizedException;
import edu.miu.horelo.dto.request.AllergyRequest;
import edu.miu.horelo.dto.response.AllergyResponse;
import edu.miu.horelo.dto.response.AllergyUserResponse;
import edu.miu.horelo.service.AllergyService;
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
@RequestMapping(value = {"/api/v1/public/auth"})
public class AllergyController {
    @Autowired
    private AllergyService allergyService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTMgmtUtilityService jwtMgmtUtilityService;
    // Create
    @PostMapping("/allergy")
    public ResponseEntity<AllergyResponse> createAllergy(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AllergyRequest allergy) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            AllergyResponse aller = allergyService.addNewAllergy(allergy, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(aller);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Read all
    @GetMapping("/allergy")
    public ResponseEntity<List<AllergyUserResponse>> getAllAllergies(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            List<AllergyUserResponse> allergies = allergyService.getAllergiesByUser(userId);
            if (allergies.isEmpty()) {
                return ResponseEntity.noContent().build();
            }else return new ResponseEntity<>(allergies,HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }

    // Read by ID
    @GetMapping("/allergy/{id}")
    public ResponseEntity<AllergyResponse> getAllergyById( @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);
            Optional<AllergyResponse> allergy = allergyService.getAllergyById(id, userId);
            return allergy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } finally{
            ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }


    }

    // Update
    @PatchMapping("/allergy/{id}")
    public ResponseEntity<AllergyResponse> updateAllergy(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id, @RequestBody AllergyRequest allergy) {

        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);
            AllergyResponse oldAllergy = allergyService.updateAllergy(id, allergy, userId);
            if (oldAllergy != null) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(oldAllergy);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Delete
    @DeleteMapping("/allergy/{id}")
    public ResponseEntity<Void> deleteIngredient(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer id) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            allergyService.deleteAllergy(id, userId);
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
