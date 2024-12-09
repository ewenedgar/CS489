package edu.miu.horelo.dto.request;

public record UserRequest(
        Integer userId,
        String email,
        UserProfileRequest userProfileRequest

) {
}
