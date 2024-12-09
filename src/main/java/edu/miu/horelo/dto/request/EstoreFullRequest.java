package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.OrderPolicy;
import jakarta.validation.constraints.NotBlank;

public record EstoreFullRequest(
        String phoneNumber,
        String name,
        String email,
        String logo,
        OrderPolicy orderPolicy,
        OpenDaysAndHoursDTO openDaysAndHoursDTO,
        String visibility,
        AddressRequest primaryAddress,
        UserRequest editor
) {
}
