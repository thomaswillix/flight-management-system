package unit.controller;

import controller.FlightController;
import model.Flight;
import service.FlightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static utils.TestDataFactory.buildValidFlight;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightControllerTest {
    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    @Test
    void whenCreateValidFlight_thenReturns200() {
        Flight flight = buildValidFlight();
        when(flightService.save(any())).thenReturn(flight);

        ResponseEntity<Flight> response = flightController.createFlight(flight);

        assertEquals(201, response.getStatusCode().value());
    }
    
    @Test
    void whenFlightExists_thenReturns200() {
        Flight flight = buildValidFlight();
        when(flightService.findByAirlineAndNumber("IB", 1L)).thenReturn(Optional.of(flight));

        ResponseEntity<Flight> response = flightController.getFlight("IB", 1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(flight, response.getBody());
    }

    @Test
    void whenFlightNotFound_thenReturns404() {
        when(flightService.findByAirlineAndNumber("IB", 99L)).thenReturn(Optional.empty());

        ResponseEntity<Flight> response = flightController.getFlight("IB", 99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void whenValidDate_thenReturns200WithFlights() {
        List<Flight> flights = List.of(buildValidFlight());
        when(flightService.findByDate("2026-03-01")).thenReturn(flights);

        ResponseEntity<?> response = flightController.getFlightsByDate("2026-03-01");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(flights, response.getBody());
    }

    @Test
    void whenInvalidDate_thenReturns400() {
        when(flightService.findByDate("invalid-date"))
                .thenThrow(new IllegalArgumentException("Formato inválido. Use YYYY-MM-DD."));

        ResponseEntity<?> response = flightController.getFlightsByDate("invalid-date");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Formato inválido. Use YYYY-MM-DD.", response.getBody());
    }

    @Test
    void whenEmptyDate_thenReturns400() {
        when(flightService.findByDate(""))
                .thenThrow(new IllegalArgumentException("La fecha no puede estar vacía."));

        ResponseEntity<?> response = flightController.getFlightsByDate("");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("La fecha no puede estar vacía.", response.getBody());
    }

}
