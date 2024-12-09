package edu.miu.horelo.dto.request;

import java.util.List;

public record AvailableDaysAndHoursDTO(
            List<OpenPeriodsForDayDTO> openPeriodsByDay
    ) {
       
    }