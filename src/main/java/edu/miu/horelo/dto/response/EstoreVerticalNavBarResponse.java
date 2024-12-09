package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.OpenDaysAndHours;
import edu.miu.horelo.model.OrderPolicy;

import java.time.LocalDateTime;

public record EstoreVerticalNavBarResponse(
        Long estoreId,
        String name,
        String email,
        String logo,
        String phoneNumber,
        String foodSafetyMessage,
        String Visibility,
        LocalDateTime lastUpdate,
        OrderPolicy orderPolicy,
        OpenDaysAndHours openDaysAndHours,
        AddressResponse primaryAddress
) {
    //
}