package edu.miu.horelo.controller;

import edu.miu.horelo.advice.UnauthorizedException;
import edu.miu.horelo.dto.request.ProductRequest;
import edu.miu.horelo.dto.response.ProductResponse;
import edu.miu.horelo.service.ProductService;
import edu.miu.horelo.service.UserService;
import edu.miu.horelo.service.util.JWTMgmtUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;



@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = {"/api/v1/public/auth"})
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private JWTMgmtUtilityService jwtMgmtUtilityService;

    // Create
    @PostMapping("/product")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ProductRequest productRequest) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            ProductResponse productResponse = productService.createProduct(productRequest, userId);
        return ResponseEntity.ok(productResponse);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Read all
    @GetMapping("/product")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    @GetMapping("/product/estore-public/{estoreId}")
    public ResponseEntity<List<ProductResponse>> getAllProductsByEstorePublic(@PathVariable Long estoreId ) {
        List<ProductResponse> products = productService.getProductsByEstore(estoreId);
        return ResponseEntity.ok(products);
    }
    // Read all
    @GetMapping("/product/estore/{estoreId}")
    public ResponseEntity<List<ProductResponse>> getAllProductsByEstore(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable Long estoreId
    ) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            List<ProductResponse> products = productService.getProductsByEstore(estoreId);
        return ResponseEntity.ok(products);
    } catch (UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    }

    // Read by ID
    @GetMapping("/product/{product_id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long product_id) {
        Optional<ProductResponse> product = productService.getProductById(product_id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PatchMapping("/product/{product_id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long product_id, @RequestBody ProductRequest productRequest) {

        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);
            ProductResponse updatedProduct = productService.updateProduct(product_id, productRequest, userId);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PatchMapping("/product/{id}/logo")
    public ResponseEntity<ProductResponse> updateEstoreLogo(@RequestHeader("Authorization") String token,
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

        ProductResponse productResponse = productService.updateProductLogo(id,  file);
        return ResponseEntity.ok(productResponse);
    }

    // Delete
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id) {
        try {
            // Validate and extract userId from token
            String token = extractBearerToken(authorizationHeader);
            Integer userId = extractUserIdFromToken(token);

            productService.deleteProduct(id, userId);
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