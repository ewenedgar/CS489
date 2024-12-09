package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.UserProfile;

public record UserProfileResponse(
        Integer userId,
        UserProfile userProfile,
        String email,
        String preferredName,
        Long defaultEstore
) {
}
