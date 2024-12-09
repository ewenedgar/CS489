package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Price;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SpecialEventResponse(

        Long id,

        String name,
        String image,

        LocalDate eventDate,

        LocalDateTime startTime,

        LocalDateTime endTime,

        String description,

        Price price,
        Long estoreId
) {
}
