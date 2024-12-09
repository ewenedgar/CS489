package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.OpenPeriod;

import java.time.LocalTime;

public record OpenPeriodDTO(
        LocalTime openTime,
        LocalTime closeTime
) {
    // Constructor to create OpenPeriodDTO from OpenPeriod entity
    public OpenPeriodDTO(OpenPeriod period) {
        this(period.getOpenTime(), period.getCloseTime());
    }


}