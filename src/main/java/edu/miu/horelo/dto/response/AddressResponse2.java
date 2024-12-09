package edu.miu.horelo.dto.response;

public record AddressResponse2(
        Integer addressId,
        String street,
        String city,
        String state,
        String zipCode) {
}
