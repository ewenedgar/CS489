package edu.miu.horelo.dto.response;

public record UserResponse(
        Integer userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email
) {
}
