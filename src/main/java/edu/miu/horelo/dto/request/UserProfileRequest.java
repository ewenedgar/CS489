package edu.miu.horelo.dto.request;

public record UserProfileRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        String title,
        String description,
        String avatar
) {
}
