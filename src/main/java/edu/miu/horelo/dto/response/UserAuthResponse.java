package edu.miu.horelo.dto.response;

public record UserAuthResponse(

        String jwtToken,
        String email,
        String username
        //UserAuthProfileResponse userAuthProfileResponse
) {
}
