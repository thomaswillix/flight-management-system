package service;

import model.Flight;
import repository.AirlineRepository;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final AirplaneRepository airplaneRepository;

    public FlightService(FlightRepository flightRepository,
                         AirportRepository airportRepository,
                         AirlineRepository airlineRepository,
                         AirplaneRepository airplaneRepository
    ) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.airplaneRepository = airplaneRepository;
    }

    public Flight save(Flight flight) {
        if (flight.getOrigin() == null || airportRepository.findById(
                flight.getOrigin().getIataCode()).isEmpty()
        )
            throw new IllegalArgumentException("El aeropuerto de origen no existe.");

        if (flight.getDestination() == null || airportRepository.findById(
                flight.getDestination().getIataCode()).isEmpty()
        )
            throw new IllegalArgumentException("El aeropuerto de destino no existe.");

        if (flight.getAirline() == null || airlineRepository.findById(
                flight.getAirline().getCode()).isEmpty()
        )
            throw new IllegalArgumentException("La aerolínea no existe.");

        if (flight.getPlane() == null || airplaneRepository.findById(
                flight.getPlane().getId()).isEmpty()
        )
            throw new IllegalArgumentException("El avión no existe.");

        return flightRepository.save(flight);
    }

    public Optional<Flight> findByAirlineAndNumber(String airlineCode, Long flightNumber) {
        return flightRepository.findByAirlineCodeAndId(airlineCode, flightNumber);
    }

    public List<Flight> findByDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank())
            throw new IllegalArgumentException("La fecha no puede estar vacía.");

        LocalDate date = parseDate(dateStr);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return flightRepository.findByEstimatedLocalDepartureTimeBetween(start, end);
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Formato inválido. Use YYYY-MM-DD.");
        }
    }
}
