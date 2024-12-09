package edu.miu.horelo.dto.request;

public record EstoreAddressDTO(
        //Integer estoreId, // n ot required because it comes along the pathvariable
        String street,
        String city,
        String state,
        String zipCode,
        String country
) {
}
