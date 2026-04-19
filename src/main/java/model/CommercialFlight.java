package model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static utils.Validation.requireNotNull;

@Getter
public class CommercialFlight extends Flight{
    public static final Long AVERAGE_PERSON_WEIGHT_KG = 50L;
    private Set<Passenger> passengers;
    private final Integer capacity;

    public CommercialFlight(String flightNumber, Airline airline, Airplane airplane, Airport origin, Airport destination,
                            LocalDateTime plannedLocalDepartureTime, LocalDateTime plannedLocalArrivalTime,
                            LocalDateTime estimatedLocalDepartureTime, LocalDateTime estimatedLocalArrivalTime,
                            LocalDateTime realLocalDepartureTime, LocalDateTime realLocalArrivalTime,
                            FlightState flightState, Set<Passenger> passengers, Integer capacity
    ) {
        super(flightNumber, airline, airplane, origin, destination, plannedLocalDepartureTime, plannedLocalArrivalTime,
                estimatedLocalDepartureTime, estimatedLocalArrivalTime, realLocalDepartureTime, realLocalArrivalTime,
                flightState
        );
        this.capacity = requireValidCapacity(capacity);
        setPassengers(passengers);
    }

    public Integer requireValidCapacity(Integer capacity) {
        requireNotNull(capacity, "Capacity");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity cannot be 0 or negative");
        return capacity;
    }

    public void setPassengers(Set<Passenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("Passengers list cannot be null or empty");
        }
        if (passengers.size() > this.capacity) {
            throw new IllegalArgumentException("Passengers cannot exceed flight capacity");
        }
        this.passengers = passengers;
    }

        @Override
    public BigDecimal getEstimatedFlightWeight() {
        return BigDecimal.valueOf(passengers.size() * AVERAGE_PERSON_WEIGHT_KG);
    }

    @Override
    public String toString() {
        return super.toString() + ", CommercialFlight{" +
                "passengers=" + passengers.toString() +
                ", capacity=" + capacity +
                '}';
    }
}
