package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.OpenPeriodsForDay;

import java.util.List;

public record OpenPeriodsForDayDTO(
        String dayOfWeek,
        List<OpenPeriodDTO> openPeriods
) {
    // Constructor to create OpenPeriodsForDayDTO from OpenPeriodsForDay entity
    public OpenPeriodsForDayDTO(OpenPeriodsForDay periodsForDay) {
        this(
                periodsForDay.getDayOfWeek(),
                periodsForDay.getOpenPeriods().stream()
                        .map(OpenPeriodDTO::new)
                        .toList()
        );
    }


}