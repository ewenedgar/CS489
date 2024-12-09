package edu.miu.horelo.dto.request;

import java.util.List;

public record UserAddressRequest(
        List<AddressRequest> primaryAddress
) {
}
