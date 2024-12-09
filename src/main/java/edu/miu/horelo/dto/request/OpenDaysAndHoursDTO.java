package edu.miu.horelo.dto.request;


import java.util.List;

public record OpenDaysAndHoursDTO(
        List<OpenPeriodsForDayDTO> openPeriodsByDay
) {

}