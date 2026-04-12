package repository;

import model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Finds flights whose estimated departure falls within the given time range (inclusive).
    List<Flight> findByEstimatedLocalDepartureTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Flight> findByAirlineCodeAndId(String code, Long id);
}
