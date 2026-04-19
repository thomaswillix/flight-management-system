package model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import notification.FlightStateListener;
import notification.FlightSubject;
import org.springframework.data.annotation.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static utils.Validation.requireNotBlank;
import static utils.Validation.requireNotNull;

@Entity
@Getter
@Setter
public class Flight implements FlightSubject {
    @Id
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;
    @ManyToOne
    @JoinColumn(name = "airplane_id")
    private Airplane airplane;
    @ManyToOne
    @JoinColumn(name = "origin_airport")
    private Airport origin;
    @ManyToOne
    @JoinColumn(name = "destination_airport")
    private Airport destination;

    private LocalDateTime plannedLocalDepartureTime;
    private LocalDateTime plannedLocalArrivalTime;

    private LocalDateTime estimatedLocalDepartureTime;
    private LocalDateTime estimatedLocalArrivalTime;

    private LocalDateTime realLocalDepartureTime;
    private LocalDateTime realLocalArrivalTime;

    private FlightState flightState;

    // --- Notifications ---
    private final List<FlightStateListener> listeners = new ArrayList<>();

    @Override
    public void addListener(FlightStateListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(FlightStateListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void notifyListeners() {
        for (FlightStateListener listener : listeners){
            listener.onStateChange(this);
        }
    }

    public Flight() {} // Empty builder for JPA

    public Flight(String flightNumber, Airline airline, Airplane airplane,
                  Airport origin, Airport destination,
                  LocalDateTime plannedLocalDepartureTime, LocalDateTime plannedLocalArrivalTime,
                  LocalDateTime estimatedLocalDepartureTime, LocalDateTime estimatedLocalArrivalTime,
                  LocalDateTime realLocalDepartureTime, LocalDateTime realLocalArrivalTime,
                  FlightState flightState) {
        this.flightNumber = requireNotBlank(flightNumber, "Flight number");
        this.airline = requireNotNull(airline, "Airline");
        this.airplane = requireNotNull(airplane, "Airplane");
        this.origin = requireNotNull(origin, "Origin airport");
        this.destination = requireNotNull(destination, "Destination airport");
        validateTimeRange(plannedLocalDepartureTime, plannedLocalArrivalTime, FlightTimeType.PLANNED);
        this.plannedLocalDepartureTime = plannedLocalDepartureTime;
        this.plannedLocalArrivalTime = plannedLocalArrivalTime;
        validateTimeRange(estimatedLocalDepartureTime, estimatedLocalArrivalTime, FlightTimeType.ESTIMATED);
        this.estimatedLocalDepartureTime = estimatedLocalDepartureTime;
        this.estimatedLocalArrivalTime = estimatedLocalArrivalTime;
        validateRealTimeRange(realLocalDepartureTime, realLocalArrivalTime);
        this.realLocalDepartureTime = realLocalDepartureTime;
        this.realLocalArrivalTime = realLocalArrivalTime;
        this.flightState = requireNotNull(flightState, "Flight State");
    }
    public void setEstimatedTimes(LocalDateTime estimatedLocalDepartureTime, LocalDateTime estimatedLocalArrivalTime) {
        validateTimeRange(estimatedLocalDepartureTime, estimatedLocalArrivalTime, FlightTimeType.ESTIMATED);
        this.estimatedLocalDepartureTime = estimatedLocalDepartureTime;
        this.estimatedLocalArrivalTime = estimatedLocalArrivalTime;
    }

    public void setRealTimes(LocalDateTime realLocalDepartureTime, LocalDateTime realLocalArrivalTime) {
        validateRealTimeRange(realLocalDepartureTime, realLocalArrivalTime);
        this.realLocalDepartureTime = realLocalDepartureTime;
        this.realLocalArrivalTime = realLocalArrivalTime;
    }

    public void setFlightState(FlightState flightState) {
        requireNotNull(flightState, "Flight State");
        this.flightState = flightState;
        notifyListeners();  // setFlightState dispara notify
    }

    private void validateTimeRange(LocalDateTime departure, LocalDateTime arrival, FlightTimeType type) {
        requireNotNull(departure, type + " departure date");
        requireNotNull(arrival, type + " arrival date");
        if (!departure.isBefore(arrival))
            throw new IllegalArgumentException(type + " departure must be before arrival");
    }

    // This validation is different due to us not knowing at first the real departure datetime and arrival datetime,
    // so they could and should be null at first
    private void validateRealTimeRange(LocalDateTime departure, LocalDateTime arrival) {
        if (departure != null && arrival != null && !departure.isBefore(arrival))
            throw new IllegalArgumentException(FlightTimeType.REAL + " departure must be before arrival");
    }

    /*
     * Calculate the duration of a flight giving the type of flightTime [planned, estimated, real]
     * and the timeUnit [seconds, minutes, hours]
     */
    public long getFlightDurationInTimeUnit(FlightTimeType type, ChronoUnit timeUnit) {
        if (type == null) throw new IllegalArgumentException("Flight time type can't be null");

        LocalDateTime departure = switch (type) {
            case PLANNED -> plannedLocalDepartureTime;
            case ESTIMATED -> estimatedLocalDepartureTime;
            case REAL -> realLocalDepartureTime;
        };
        LocalDateTime arrival = switch (type) {
            case PLANNED -> plannedLocalArrivalTime;
            case ESTIMATED -> estimatedLocalArrivalTime;
            case REAL -> realLocalArrivalTime;
        };
        if (departure == null || arrival == null) {
            throw new IllegalStateException("Flight times not available for type: " + type);
        }
        return calculateDurationInTimeUnit(
                departure, arrival, timeUnit, origin.getTimeZone(), destination.getTimeZone()
        );
    }

    private static long calculateDurationInTimeUnit(LocalDateTime start, LocalDateTime end, ChronoUnit timeUnit,
                                                    ZoneId originTimeZone, ZoneId destinationTimeZone) {
        if (start == null) throw new IllegalArgumentException("Start date can't be null");
        if (end == null) throw new IllegalArgumentException("End date can't be null");
        if (timeUnit == null) throw new IllegalArgumentException("Time unit can't be null");

        ZonedDateTime zonedStart = start.atZone(originTimeZone);
        ZonedDateTime zonedEnd = end.atZone(destinationTimeZone);

        return timeUnit.between(zonedStart, zonedEnd);
    }

    // Comparison of real departure and arrival DateTime's converting them first into UTC time;
    public boolean arrivesSameDay() {
        ZonedDateTime zonedDeparture = realLocalDepartureTime.atZone(origin.getTimeZone())
                .withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime zonedArrival = realLocalArrivalTime.atZone(destination.getTimeZone())
                .withZoneSameInstant(ZoneId.of("UTC"));
        return zonedDeparture.toLocalDate().isEqual(zonedArrival.toLocalDate());
    }

    public BigDecimal getEstimatedFlightWeight() {
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightNumber=" + flightNumber +
                ", airline=" + airline +
                ", origin=" + origin +
                ", destination=" + destination +
                ", plannedLocalDepartureTime=" + plannedLocalDepartureTime +
                ", plannedLocalArrivalTime=" + plannedLocalArrivalTime +
                ", estimatedLocalDepartureTime=" + estimatedLocalDepartureTime +
                ", estimatedLocalArrivalTime=" + estimatedLocalArrivalTime +
                ", realLocalDepartureTime=" + realLocalDepartureTime +
                ", realLocalArrivalTime=" + realLocalArrivalTime +
                ", flightState=" + flightState +
                ", Arrives the same day=" + arrivesSameDay() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(flightNumber, flight.flightNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(flightNumber);
    }
}
