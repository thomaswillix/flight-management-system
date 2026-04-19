package repository;

import model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightNewRepository  extends JpaRepository<Flight, Long> {
    List<Flight> findByEstimatedLocalDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    Optional<Flight> findByAirlineCodeAndId(String code, Long id);

    List<Flight> findAllFlights();
    List<Flight> findAllFlightsByAirline(String airline);
    List<Flight> findAllFlightsByDeparture(String iataCode);
    Flight findFlightByNumber(String number); //The new format is "AirlineCode|FlightNumber"
    Flight save(Flight flight);
    List<Flight> findFlightsByDate(String date); //The new Format is String instead of LocalDate
}