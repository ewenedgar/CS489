package edu.miu.horelo.controller;


import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.*;
import edu.miu.horelo.dto.response.*;
import edu.miu.horelo.model.*;
import edu.miu.horelo.dto.request.*;
import edu.miu.horelo.dto.response.*;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.User;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.EstoreService;
import edu.miu.horelo.service.util.JWTMgmtUtilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = {"/api/v1/public/auth"})
@RequiredArgsConstructor
public class EstoreController {
    private final JWTMgmtUtilityService jwtMgmtUtilityService;
    private final UserRepository userRepository;
    private final EstoreService estoreService;
    @GetMapping("/estore/hello")
    public String hello() {
        return "Hello";
    }
    @PostMapping("/estore")
    public EstoreResponse createNewStore(@RequestHeader("Authorization") String token,
                                         @Valid @RequestBody EstoreRequest estoreRequest) throws BadRequestException {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
        Optional<User> userResponse = userRepository.findUserByEmail(email);
        if (userResponse.isPresent()) {
            var userId = userResponse.get().getUserId();
            System.out.println("This is the user id : "+userResponse.get().getUserId());
            return estoreService.addNewStore(userId, estoreRequest);
        }

        return null;
    }

    @GetMapping("/estore")
    public ResponseEntity<List<EstoreDTORes>> getStoreByOwner(
            @RequestHeader("Authorization") String token){

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
        Optional<User> user = Optional.of(userRepository.findUserByEmail(email)).orElseThrow(
                () -> new ResourceNotFoundException("user not found")
        );
        return user.map(value -> ResponseEntity.ok(estoreService.getStoreByUser(value.getUserId()))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }
    @PatchMapping("/estore/{estoreId}/address")
    public ResponseEntity<Estore> updateAddress(@PathVariable Long estoreId,
                                                @RequestBody EstoreAddressDTO address) {
        Estore updatedEstore = estoreService.updateEstoreAddress(estoreId, address);
        return ResponseEntity.ok(updatedEstore);
    }
    @PatchMapping("/estore/{id}")
    public ResponseEntity<EstoreResponse> updateEstore(
            @PathVariable Long id,
            @RequestBody EstoreDTO estoreDetails,
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
       User user = userRepository.getUserByEmail(email);
        Estore savedEdstore = estoreService.updateEstore1(id, estoreDetails, user);
        // Further processing...
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new EstoreResponse(
                savedEdstore.getEstoreId(),
                savedEdstore.getName(),
                savedEdstore.getEmail(),
                savedEdstore.getLogo(),
                savedEdstore.getPhoneNumber(),
                savedEdstore.getVisibility(),
                savedEdstore.getFoodSafetyMessage(),
                savedEdstore.getLastUpdate(),
                savedEdstore.getOrderPolicy(),
                new AddressResponse(
                        savedEdstore.getPrimaryAddress().getAddressId(),
                        savedEdstore.getPrimaryAddress().getStreet(),
                        savedEdstore.getPrimaryAddress().getCity(),
                        savedEdstore.getPrimaryAddress().getState(),
                        savedEdstore.getPrimaryAddress().getZipCode(),
                        savedEdstore.getPrimaryAddress().getCountry()
                ), savedEdstore.getOpenDaysAndHours(),
                savedEdstore.getEditor()
                //savedEdstore.getCreatorId()
        )
        );
    }

    @PatchMapping("/estore/{estoreId}/open-hours")
    public ResponseEntity<EstoreResponse> updateOpenHours(@PathVariable Long estoreId, @RequestBody OpenDaysAndHoursDTO openDaysAndHours) {
        Estore updatedEstore = estoreService.updateOpenHours(estoreId, openDaysAndHours);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new EstoreResponse(
                updatedEstore.getEstoreId(),
                updatedEstore.getName(),
                updatedEstore.getEmail(),
                updatedEstore.getLogo(),
                updatedEstore.getPhoneNumber(),
                updatedEstore.getFoodSafetyMessage(),
                updatedEstore.getVisibility(),
                updatedEstore.getLastUpdate(),
                        updatedEstore.getOrderPolicy(),
                        updatedEstore.getPrimaryAddress() != null
                        ? new AddressResponse(
                        updatedEstore.getPrimaryAddress().getAddressId(),
                        updatedEstore.getPrimaryAddress().getStreet(),
                        updatedEstore.getPrimaryAddress().getCity(),
                        updatedEstore.getPrimaryAddress().getState(),
                        updatedEstore.getPrimaryAddress().getZipCode(),
                        updatedEstore.getPrimaryAddress().getCountry()
                )
                        : null,
                updatedEstore.getOpenDaysAndHours(),
                updatedEstore.getEditor()
                )
        );
    }

    @GetMapping("/estore/{id}")
    public ResponseEntity<EstoreVerticalNavBarResponse> getEStoreVerticalNavBarById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        // Remove "Bearer " prefix from the token if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract email from the token and fetch the user
        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
        Optional<User> userResponse = userRepository.findUserByEmail(email);

        // If the user is not found, return 403 Forbidden
        if (userResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Fetch the estore by id from the service and handle the result
        return estoreService.getStoreById(id)
                .map(ResponseEntity::ok) // Return 200 OK with the estore response if found
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Return 404 if not found
    }


    @PutMapping("/estore/{id}")
    public ResponseEntity<?> updateStore(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody EstoreFullRequest estoreRequest
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
        Optional<User> userResponse = userRepository.findUserByEmail(email);
        if (userResponse.isPresent()) {
            var userId = userResponse.get().getUserId();
            var res = estoreService.updateStore(id, userId, estoreRequest);
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found or unauthorized");
    }
@PatchMapping("/estore/{id}/logo")
    public ResponseEntity<EstoreResponse> updateEstoreLogo(@RequestHeader("Authorization") String token,
                                                           @PathVariable("id") Long id,  // Handle user profile data
                                                           @RequestPart(value="file", required = false) MultipartFile file // Handle file upload (optional)
) {

    if (file == null|| file.isEmpty()) {

        System.out.println("No file received " + id );
        return ResponseEntity.badRequest().body(null);
    } else {
        System.out.println("Received file: " + file.getOriginalFilename());
    }

   // String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
    if (token.startsWith("Bearer ")) {
        token = token.substring(7);
    }

    String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
    Optional<User> userResponse = userRepository.findUserByEmail(email);
    if (userResponse.isPresent()) {
        var user = userResponse.get();
        EstoreResponse estoreResponse = estoreService.updateEstoreLogo(id, file, user);
        return ResponseEntity.ok(estoreResponse);
    }
    return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/estore/{id}/foodSafetyMessage")
    public ResponseEntity<FoodSafetyMessageResponse> updateEstoreFoodSafetyMessage(
                                                           @PathVariable("id") Long estoreId,
                                                           @RequestBody FoodSafetyMessageRequest foodSafetyMessageRequest
    ) {


        // String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));

        FoodSafetyMessageResponse foodSafetyResponse = estoreService.updateEstoreFoodSafetyMessage(estoreId,  foodSafetyMessageRequest);
        return ResponseEntity.ok(foodSafetyResponse);
    }

}