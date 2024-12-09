package edu.miu.horelo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDaysAndHours {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "food_item_id")
    private List<OpenPeriodsForDay> openPeriodsByDay;

    public void setOpenPeriodsByDay(List<OpenPeriodsForDay> openPeriodsByDay) {
        this.openPeriodsByDay = openPeriodsByDay;
    }

    public boolean isOpen(String dayOfWeek, LocalTime time) {
        for (OpenPeriodsForDay periodsForDay : openPeriodsByDay) {
            if (periodsForDay.getDayOfWeek().equals(dayOfWeek)) {
                for (OpenPeriod period : periodsForDay.getOpenPeriods()) {
                    if (!time.isBefore(period.getOpenTime()) && !time.isAfter(period.getCloseTime())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}