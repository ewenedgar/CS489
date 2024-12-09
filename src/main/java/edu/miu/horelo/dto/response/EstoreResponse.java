package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.OpenDaysAndHours;
import edu.miu.horelo.model.OrderPolicy;
import edu.miu.horelo.model.User;

import java.time.LocalDateTime;

public record EstoreResponse(
        Long estoreId,
        String name,
        String email,
        String logo,
        String phoneNumber,
        String foodSafetyMessage,
        String Visibility,
        LocalDateTime lastUpdate,
        OrderPolicy orderPolicy,
        AddressResponse primary_address,
        OpenDaysAndHours openDaysAndHours,
        User editor
) {
    //
}
