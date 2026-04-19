package model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import static utils.Validation.requireValidWeight;

public class CargoFlight extends Flight{
    private final BigDecimal cargoWeight;

    public CargoFlight(String flightNumber, Airline airline, Airplane airplane, Airport origin, Airport destination,
                       LocalDateTime plannedLocalDepartureTime, LocalDateTime plannedLocalArrivalTime,
                       LocalDateTime estimatedLocalDepartureTime, LocalDateTime estimatedLocalArrivalTime,
                       LocalDateTime realLocalDepartureTime, LocalDateTime realLocalArrivalTime,
                       FlightState flightState, BigDecimal cargoWeight
    ) {
        super(flightNumber, airline, airplane, origin, destination, plannedLocalDepartureTime, plannedLocalArrivalTime,
                estimatedLocalDepartureTime, estimatedLocalArrivalTime, realLocalDepartureTime,
                realLocalArrivalTime, flightState
        );
        this.cargoWeight = requireValidWeight(cargoWeight, "Cargo");
    }

    @Override
    public BigDecimal getEstimatedFlightWeight() {
        return cargoWeight.add(getAirplane().getAirplaneWeight());
    }

    @Override
    public String toString() {
        return super.toString() + ", CargoFlight{" +
                "cargoWeight=" + cargoWeight +
                '}';
    }
}
