package edu.miu.horelo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "special_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long specialEventId;

    @Column(nullable = false)
    private String name;

    private String image;
    @Column(nullable = true)
    private LocalDate eventDate;

    @Column(nullable = true)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(length = 500)
    private String description;

    @Embedded
    private Price price;
    @JsonIgnore
    @JoinColumn(name="estore_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Estore estore;

public SpecialEvent(String name,String description,LocalDate eventDate,Estore estore ){
    this.name = name;
    this.eventDate = eventDate;
    this.estore = estore;
    this.description = description;
}
    @Override
    public String toString() {
        return "SpecialEvent{" +
               "specialEventId=" + specialEventId +
               ", name='" + name + '\'' +
               ", eventDate=" + eventDate +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", estore=" + estore +
               '}';
    }
}
