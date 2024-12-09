package edu.miu.horelo.dto.response;

public record AddressResponse(
        Integer addressId,
        String street,
        String city,
        String state,
        String zipCode,
        String country
) {
}
