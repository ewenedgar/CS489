package edu.miu.horelo.dto.request;

public record AddressRequest(
        String street,
        String city,
        String state,
        String zipCode
) {
}
