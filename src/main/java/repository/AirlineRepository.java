package repository;

import model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirlineRepository extends JpaRepository<Airline, String> {
    List<Airline> findAllAirlines();
    Airline findAirlineByCode(String airlineCode);
}
