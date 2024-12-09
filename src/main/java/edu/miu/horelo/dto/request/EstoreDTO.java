package edu.miu.horelo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.miu.horelo.model.OrderPolicy;

public record EstoreDTO(
        String phoneNumber,
        String name,
        String email,
        @JsonProperty("message")String foodSafetyMessage,
        String timeZone,
        OrderPolicy orderPolicy,
        EstoreAddressDTO primaryAddress,
        @JsonProperty("openDaysAndHours")OpenDaysAndHoursDTO openDaysAndHoursDTO
) {
    // You can add any additional validation or methods here if needed
}