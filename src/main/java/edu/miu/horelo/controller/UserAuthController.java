package edu.miu.horelo.controller;

import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.dto.request.UserAuthRequest;
import edu.miu.horelo.dto.request.UserRequest;
import edu.miu.horelo.dto.request.defaultEstoreRequest;
import edu.miu.horelo.dto.response.UserAuthResponse;
import edu.miu.horelo.dto.response.UserInfoResponse;
import edu.miu.horelo.dto.response.UserProfileResponse;
import edu.miu.horelo.model.Role;
import edu.miu.horelo.model.User;
import edu.miu.horelo.model.UserEstoreRole;
import edu.miu.horelo.service.FileManagerService;
import edu.miu.horelo.service.RoleService;
import edu.miu.horelo.service.UserEstoreRoleService;
import edu.miu.horelo.service.UserService;
import edu.miu.horelo.service.util.JWTMgmtUtilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;


@RestController
@CrossOrigin
@RequestMapping(value = {"/api/v1/public/auth"})
@RequiredArgsConstructor
public class UserAuthController {
    private final FileManagerService fileManagerService;
    private final JWTMgmtUtilityService jwtMgmtUtilityService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserEstoreRoleService userEstoreRoleService;
    private final RoleService roleService;



    @PostMapping("/upload")
    public ResponseEntity<SavedFileDTO> uploadFileTwo(
            @RequestParam("file") MultipartFile file) {

        String imgFolderName = "user";
        return getSavedFileDTOResponseEntity(file, fileManagerService, imgFolderName);
    }

    static ResponseEntity<SavedFileDTO> getSavedFileDTOResponseEntity(
            @RequestParam("file") MultipartFile file,
            FileManagerService fileManagerService,
            String imgFolderName) {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFile(file);
        fileDTO.setFileName(file.getOriginalFilename());
        try {
            // Use the service to upload the file
            SavedFileDTO savedFile = fileManagerService.uploadFile(fileDTO, imgFolderName, imgFolderName);

            // Return the result
            return new ResponseEntity<>(savedFile, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
    @GetMapping("/user/{id}/info")
    public UserInfoResponse getUserinfo(@PathVariable Integer id){
        Optional<UserInfoResponse> u = ofNullable(userService.getUserInfoById(id));

        return u.orElse(null);
    }
    @GetMapping("/user")
    public ResponseEntity<UserProfileResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));

        UserProfileResponse  userProfileResponse = userService.getUserProfileByEmail(email);

        try {

            if (userProfileResponse != null ) {
                return ResponseEntity.ok(userProfileResponse);
            } else {
                return ResponseEntity.notFound().build(); // Or return an appropriate error response
            }
        } catch (Exception ex) {
            System.out.println("Exception while getting user profile: " + ex);
            throw ex;
        }


    }


    @PostMapping(value = {"/login"})
    public ResponseEntity<UserAuthResponse> authenticateUser(@Valid @RequestBody UserAuthRequest userAuthRequest) {
        UserAuthResponse userAuthResponse = null;
        try {
            String email = userAuthRequest.email();
            String password = userAuthRequest.password();

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Retrieve user by email
            User user = userService.getUserByEmail(email);
            // Generate JWT token
            String jwtToken = jwtMgmtUtilityService.generateToken(user);



            if (user != null ) {
                userAuthResponse = new UserAuthResponse(jwtToken, user.getEmail(), user.getUsername());
            }
        } catch (Exception ex) {
            System.out.println("UserAuthException is: " + ex);
            throw ex;
        }
        return ResponseEntity.ok(userAuthResponse);
    }
    @PostMapping(value = {"/register"})
    public ResponseEntity<UserAuthResponse> registerNewUser(@Valid @RequestBody UserAuthRequest userAuthRequest) {
        try {
            // Convert UserAuthRequest to User entity
            User user = new User();
            user.setEmail(userAuthRequest.email());
            user.setPreferredName(userAuthRequest.username());
            user.setUsername(userAuthRequest.email());
            user.setPassword(userAuthRequest.password());


            // Register the new user
            User registeredUser = userService.registerNewUser(user);

            if (registeredUser != null) {
                // Assign the USER role to the newly registered user
                // Assuming 'roleService' is a service that fetches roles from the database
                Role userRole = roleService.findByRoleName("ROLE_USER"); // Replace with actual method to find the USER role

                if (userRole != null) {
                    // Create a new UserEstoreRole and assign it to the user
                    UserEstoreRole userEstoreRole = new UserEstoreRole();
                    userEstoreRole.setCreatedBy(registeredUser);
                    userEstoreRole.setUser(registeredUser);
                    userEstoreRole.setRole(userRole);
                    userEstoreRole.setAssignedDate(LocalDate.now()); // Set the role assignment date
                    userEstoreRole.setAcceptedRole(true); // The role is accepted
                     // Set active flag to true

                    // Assuming there's a service for UserEstoreRole to persist the relation
                    userEstoreRoleService.assignRoleUserToUser(registeredUser.getUserId(), null, userRole,registeredUser);
                }
                // Generate JWT token for the new user
                String jwtToken = jwtMgmtUtilityService.generateToken(registeredUser);

                UserAuthResponse userAuthResponse = new UserAuthResponse(jwtToken, registeredUser.getEmail(), registeredUser.getUsername());

                return ResponseEntity.ok(userAuthResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception ex) {
            System.out.println("UserAuthException is: " + ex);
            throw ex;
        }
    }
    @PatchMapping("/user/{userId}/allergies/{allergyId}")
    public ResponseEntity<User> assignAllergyToUser(@PathVariable Integer userId, @PathVariable Integer allergyId) {
        User updatedUser = userService.assignAllergyToUser(userId, allergyId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
    @PatchMapping("/user/{userId}/allergies")
    public ResponseEntity<User> assignMultipleAllergiesToUser(@PathVariable Integer userId, @RequestBody List<Integer> allergyIds) {
        User updatedUser = userService.assignMultipleAllergiesToUser(userId, allergyIds);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PatchMapping("/user")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestPart("userRequest") UserRequest userRequest,  // Handle user profile data
            @RequestPart(value = "file", required = false) MultipartFile file // Handle file upload (optional)
    ) {
        System.out.println("Received userProfile JSON: " + userRequest);
        if (file != null) {
            System.out.println("Received file: " + file.getOriginalFilename());
        } else {
            System.out.println("No file received");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));

        UserProfileResponse userProfileResponse = userService.updateUserProfile(email, userRequest, file);
        return ResponseEntity.ok(userProfileResponse);
    }
    @GetMapping("user/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Integer id) {
        UserProfileResponse userProfileResponse = userService.getUserById(id);
        if (userProfileResponse != null) {
            return ResponseEntity.ok(userProfileResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/user/upload")
    public ResponseEntity<String> uploadUserProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));  // Extract userId from the JWT token

        try {
            String fileName = userService.uploadUserProfileImage(file, email);
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }


    @PatchMapping("/user/estore")
    public ResponseEntity<UserProfileResponse> setDefaultEstore(@Valid @RequestHeader("Authorization") String token,
                                                                @RequestBody defaultEstoreRequest request) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract userId from token
        String email = jwtMgmtUtilityService.extractClaim(token, claims -> claims.get("sub", String.class));
        Long estoreId = request.estoreId();
        // Check if the userId is present
            try {
                // Set the default estore for the user
                userService.setDefaultEstore(email, estoreId);
                UserProfileResponse userProfileResponse = userService.getUserProfileByEmail(email);
                return ResponseEntity.ok(userProfileResponse);
            } catch (Exception e) {
                // Handle any exceptions during the process
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

    }

}
