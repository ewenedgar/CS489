package edu.miu.horelo.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.dto.request.EstoreRequestId;
import edu.miu.horelo.dto.request.UserRequest;
import edu.miu.horelo.dto.response.AllergyResponse;
import edu.miu.horelo.dto.response.UserInfoResponse;
import edu.miu.horelo.dto.response.UserProfileResponse;
import edu.miu.horelo.model.Allergy;
import edu.miu.horelo.model.User;
import edu.miu.horelo.model.UserProfile;
import edu.miu.horelo.repository.AllergyRepository;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.FileManagerService;
import edu.miu.horelo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileManagerService fileManagerService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private AllergyRepository allergyRepository;

    public User assignAllergyToUser(Integer userId, Integer allergyId) {
        // Fetch user and allergy
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Allergy allergy = allergyRepository.findById(allergyId).orElseThrow(() -> new ResourceNotFoundException("Allergy not found"));

        // Assign the allergy to the user
        user.getAllergyList().add(allergy);

        // Save the user to update the relationship
        return userRepository.save(user);
    }
    public User assignMultipleAllergiesToUser(Integer userId, List<Integer> allergyIds) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Allergy> allergies = allergyRepository.findAllById(allergyIds);
        user.getAllergyList().addAll(allergies);

        return userRepository.save(user);
    }

    private final String bucketName = "horeloimgs";  // Replace with your bucket

    public UserServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }
    public static String extractUsername(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.substring(0, atIndex);
    }
    public String uploadUserProfileImage(MultipartFile file, String email) throws IOException {
        String imageName = file.getOriginalFilename();
        String key = "img_" +extractUsername(email) + imageName;

        // Upload the file to S3
        s3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), null));

        // Return the name of the file (key)
        return imageName;
    }

    @Override
    public UserProfileResponse setDefaultEstore(String email, Long estoreId) {
        // Retrieve the user by email
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Set the default estore
        user.setDefaultEstore(estoreId);

        // Save the user and return the UserProfileResponse
        User updatedUser = userRepository.save(user);

        return new UserProfileResponse(
                updatedUser.getUserId(),
                updatedUser.getUserProfile(),
                updatedUser.getEmail(),
                updatedUser.getPreferredName(),
                updatedUser.getDefaultEstore()
        );
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElse(null);
    }
    @Override
    public UserProfileResponse getUserById(Integer id) {
        return userRepository.findById(id)
                .map(user -> new UserProfileResponse(
                        user.getUserId(),
                        user.getUserProfile(),
                        user.getEmail(),
                        user.getPreferredName(),
                        user.getDefaultEstore()
                ))
                .orElse(null);
    }

    @Override
    public UserInfoResponse getUserInfoById(Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            List<AllergyResponse> allergyResponses = user.getAllergyList() != null ?
                    user.getAllergyList().stream().map(
                            allergy -> new AllergyResponse(
                                    allergy.getAllergyId(),
                                    allergy.getName(),
                                    allergy.getDescription(),
                                    allergy.getScale()
                            )
                    ).toList() : Collections.emptyList();

            return new UserInfoResponse(user.getUserId(),
                    user.getUserProfile().getFirstName(),
                    user.getUserProfile().getLastName(),
                    user.getUserProfile().getPhoneNumber(),
                    user.getEmail(),
                    /*(user.getPrimary_address() != null) ?
                            userAdd(id) : null,*/
                    allergyResponses

            );
        } else {
            throw new RuntimeException("User not found with id: " + id);

        }
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByUsername(email)
                .orElse(null);
    }


    @Override
    public UserProfileResponse getUserProfileByEmail(String email) {
        return userRepository.findUserByUsername(email)
                .map(user -> new UserProfileResponse(
                        user.getUserId(),
                        user.getUserProfile(),
                        user.getEmail(),
                        user.getPreferredName(),
                        user.getDefaultEstore()
                ))
                .orElse(null);
    }

    @Override
    public User registerNewUser(User user) {
        // Check if the user already exists

        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with Email already exists");
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        // Save the user to the database
        return userRepository.save(user);
    }



    @Override
    public UserProfileResponse updateUserProfile(String email, UserRequest userRequest, MultipartFile file) {
        // Find the existing user by email
        User existingUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Updating user profile for: " + existingUser.getUsername());

        // Validate that userProfileRequest is not null
        if (userRequest == null || userRequest.userProfileRequest() == null) {
            throw new IllegalArgumentException("UserProfileRequest cannot be null");
        }

        // Create a UserProfile object from the request
        UserProfile userProfile = new UserProfile(
                userRequest.userProfileRequest().firstName(),
                userRequest.userProfileRequest().lastName(),
                userRequest.userProfileRequest().phoneNumber(),
                userRequest.userProfileRequest().title(),
                userRequest.userProfileRequest().description(),
                userRequest.userProfileRequest().avatar() // This will be updated if a new file is uploaded
        );

        // Check if a file is present and handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                // Create a FileDTO instance
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFile(file); // Set the MultipartFile
                fileDTO.setFileName(file.getOriginalFilename()); // Set the original file name

                // Get the existing avatar filename for deletion
                String oldAvatar = existingUser.getUserProfile().getAvatar();

                // Call fileManagerService to upload the file and delete the old one
                SavedFileDTO savedFile = fileManagerService.uploadFile(fileDTO, "user", oldAvatar); // null for default folder

                // Set the new avatar URL based on the uploaded file
                userProfile.setAvatar(savedFile.getGeneratedFileName()); // Assuming you store the file URL or name
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file", e);
            }
        }

        // Update the existing user's profile details
        existingUser.setUserProfile(userProfile);

        // Save the updated user back to the repository
        User updatedUser = userRepository.save(existingUser);

        // Return the updated user profile response
        return new UserProfileResponse(
                updatedUser.getUserId(),
                updatedUser.getUserProfile(),
                updatedUser.getEmail(),
                updatedUser.getPreferredName(),
                updatedUser.getDefaultEstore()
        );
    }

    @Override
    public UserProfileResponse addUserProfile(UserRequest userRequest) {
        UserProfile userProfile = new UserProfile(
                userRequest.userProfileRequest().firstName(),
                userRequest.userProfileRequest().lastName(),
                userRequest.userProfileRequest().phoneNumber(),
                userRequest.userProfileRequest().title(),
                userRequest.userProfileRequest().description(),
                userRequest.userProfileRequest().avatar()
        );

        User user = new User();
        user.setEmail(userRequest.email());
        user.setUserProfile(userProfile);
        user.setPreferredName(userProfile.getFirstName() + " " + userProfile.getLastName());

        User savedUser = userRepository.save(user);

        return new UserProfileResponse(
                savedUser.getUserId(),
                savedUser.getUserProfile(),
                savedUser.getEmail(),
                savedUser.getPreferredName(),
                savedUser.getDefaultEstore()
        );
    }
    public void setDefaultEstore(Integer userId, EstoreRequestId estoreRequestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDefaultEstore(estoreRequestId.estoreId());
        userRepository.save(user);
    }
    public Optional<Integer> findUserIdByEmail(String email) {
        return userRepository.findUserIdByEmail(email);
    }
}
