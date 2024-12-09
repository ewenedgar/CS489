package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.UserRequest;
import edu.miu.horelo.dto.response.UserInfoResponse;
import edu.miu.horelo.dto.response.UserProfileResponse;
import edu.miu.horelo.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User assignMultipleAllergiesToUser(Integer userId, List<Integer> allergyIds);
    User assignAllergyToUser(Integer userId, Integer allergyId);
    User getUserByUsername(String username);
    UserProfileResponse getUserById(Integer id);
    UserInfoResponse getUserInfoById(Integer id);
    User getUserByEmail(String email);
    UserProfileResponse getUserProfileByEmail(String email);

    User registerNewUser(User user);

    UserProfileResponse updateUserProfile(String email, UserRequest userRequest, MultipartFile file);

    UserProfileResponse addUserProfile(UserRequest userProfileRequest);
    String uploadUserProfileImage(MultipartFile file, String userId) throws IOException, IOException;

    UserProfileResponse setDefaultEstore(String email, Long estoreId);
    Optional<Integer> findUserIdByEmail(String email);
}
