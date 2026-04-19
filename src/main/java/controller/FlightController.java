package controller;

import model.Flight;
import service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping()
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight){
        Flight savedFlight = flightService.save(flight);
        URI location = URI.create("/api/flights/" + savedFlight.getAirline().getAirlineCode() + "/" + savedFlight.getFlightNumber());
        return ResponseEntity.created(location).body(savedFlight);
    }

    @GetMapping("/{airlineCode}/{flightNumber}")
    public ResponseEntity<Flight> getFlight(
            @PathVariable String airlineCode,
            @PathVariable Long flightNumber
    ){
        Optional<Flight> flight = flightService.findByAirlineAndNumber(airlineCode, flightNumber);

        return flight.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<?> getFlightsByDate(@RequestParam String date) {
        try {
            List<Flight> flights = flightService.findByDate(date);
            return ResponseEntity.ok(flights);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
