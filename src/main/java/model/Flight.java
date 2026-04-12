package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "origin_airport")
    private Airport origin;

    @ManyToOne
    @JoinColumn(name = "destination_airport")
    private Airport destination;

    private LocalDateTime estimatedLocalDepartureTime;
    private LocalDateTime estimatedLocalArrivalTime;

    private LocalDateTime realLocalDepartureTime;
    private LocalDateTime realLocalArrivalTime;

    @ManyToOne
    @JoinColumn(name = "airplane_id")
    private Airplane plane;

    public long getEstimatedFlightDurationInMinutes() {
        return calculateDuration(
                estimatedLocalDepartureTime, origin.getTimeZone(),
                estimatedLocalArrivalTime, destination.getTimeZone()
        );
    }

    public long getRealFlightDurationInMinutes() {
        return calculateDuration(
                realLocalDepartureTime, origin.getTimeZone(),
                realLocalArrivalTime, destination.getTimeZone()
        );
    }

    private long calculateDuration(LocalDateTime start, ZoneId startZone,
                                   LocalDateTime end, ZoneId endZone) {
        if (start == null || end == null) return 0;

        ZonedDateTime zonedStart = start.atZone(startZone);
        ZonedDateTime zonedEnd = end.atZone(endZone);

        if (zonedStart.isAfter(zonedEnd)) return 0;

        return Duration.between(zonedStart, zonedEnd).toMinutes();
    }
}
