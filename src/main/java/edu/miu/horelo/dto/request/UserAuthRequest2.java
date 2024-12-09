package edu.miu.horelo.dto.request;
public record UserAuthRequest2(
        String username,
        String email,
        String password
) {
}
