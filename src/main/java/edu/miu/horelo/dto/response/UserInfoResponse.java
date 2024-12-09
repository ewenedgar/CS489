package edu.miu.horelo.dto.response;

import java.util.List;

public record UserInfoResponse(
        Integer userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        //AddressResponse primaryAddress,
        List<AllergyResponse> allergy
) {
}
