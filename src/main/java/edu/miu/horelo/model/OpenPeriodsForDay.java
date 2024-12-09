package edu.miu.horelo.model;import jakarta.persistence.*;
import java.util.List;

@Entity
public class OpenPeriodsForDay {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String dayOfWeek;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "open_periods_for_day_id")
    private List<OpenPeriod> openPeriods;

    public OpenPeriodsForDay() {}

    public OpenPeriodsForDay(String dayOfWeek, List<OpenPeriod> openPeriods) {
        this.dayOfWeek = dayOfWeek;
        this.openPeriods = openPeriods;
    }

    public Long getId() {
        return id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<OpenPeriod> getOpenPeriods() {
        return openPeriods;
    }

    public void setOpenPeriods(List<OpenPeriod> openPeriods) {
        this.openPeriods = openPeriods;
    }
}
