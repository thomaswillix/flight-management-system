package adapter;

import model.Airline;
import model.Flight;
import repository.FlightNewRepository;
import repository.FlightRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FlightRepositoryAdapter implements FlightRepository {

    private final FlightNewRepository flightNewRepository;

    public FlightRepositoryAdapter(FlightNewRepository flightNewRepository) {
        this.flightNewRepository = flightNewRepository;
    }

    @Override
    public List<Flight> findByEstimatedLocalDepartureTimeBetween(LocalDateTime start, LocalDateTime end) {
        return flightNewRepository.findByEstimatedLocalDepartureTimeBetween(start, end);
    }

    @Override
    public Optional<Flight> findByAirlineCodeAndId(String code, Long id) {
        return flightNewRepository.findByAirlineCodeAndId(code, id);
    }

    @Override
    public List<Flight> findAllFlights() {
        return flightNewRepository.findAllFlights();
    }

    @Override
    public List<Flight> findAllFlightsByAirline(String airline) {
        return flightNewRepository.findAllFlightsByAirline(airline);
    }

    @Override
    public List<Flight> findAllFlightsByDeparture(String iataCode) {
        return flightNewRepository.findAllFlightsByDeparture(iataCode);
    }

    @Override
    public Flight saveFlight(Flight flight) {
        return flightNewRepository.save(flight);
    }

    // Methods with new implementations
    @Override
    public Flight findFlightByNumber(Airline airline, String number) {
        String adapted = airline.getAirlineCode() + "|" + number; // new format
        return flightNewRepository.findFlightByNumber(adapted);
    }

    @Override
    public List<Flight> findFlightsByDate(LocalDate date) {
        String adapted = date.toString(); // LocalDate → String
        return flightNewRepository.findFlightsByDate(adapted);
    }
}
