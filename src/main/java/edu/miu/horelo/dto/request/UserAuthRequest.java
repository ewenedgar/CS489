package edu.miu.horelo.dto.request;

public record UserAuthRequest(
        String username,
        String email,
        String password
) {
}
