package unit.service;

import model.Flight;
import repository.AirlineRepository;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;
import service.FlightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static utils.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock private FlightRepository flightRepository;
    @Mock private AirportRepository airportRepository;
    @Mock private AirlineRepository airlineRepository;
    @Mock private AirplaneRepository airplaneRepository;

    @InjectMocks private FlightService flightService;

    @Test
    void whenSearchByDate_thenReturnFlightList() {
        LocalDateTime now = LocalDateTime.now();
        Flight mockFlight = new Flight();
        mockFlight.setEstimatedLocalDepartureTime(now);

        when(flightRepository.findByEstimatedLocalDepartureTimeBetween(any(), any()))
                .thenReturn(List.of(mockFlight));

        List<Flight> result = flightService.findByDate(String.valueOf(now.toLocalDate()));
        System.out.println(result.get(0).toString());

        assertFalse(result.isEmpty());
        assertEquals(now, result.get(0).getEstimatedLocalDepartureTime());
        verify(flightRepository, times(1)).findByEstimatedLocalDepartureTimeBetween(any(), any());
    }

    @Test
    void whenSearchByBlank_thenReturnError() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flightService.findByDate(""));

        assertEquals("La fecha no puede estar vacía.", exception.getMessage());

        verifyNoInteractions(flightRepository);
    }

    @Test
    void whenSearchByInvalidDateFormat_thenReturnError() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flightService.findByDate("invalid-date"));

        assertEquals("Formato inválido. Use YYYY-MM-DD.", exception.getMessage());

        verifyNoInteractions(flightRepository);
    }

    @Test
    void whenOriginNotFoundInDB_thenThrowsException() {
        Flight flight = buildValidFlight();
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("El aeropuerto de origen no existe.", ex.getMessage());
    }

    @Test
    void whenDestinationNotFoundInDB_thenThrowsException() {
        Flight flight = buildValidFlight();
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));
        when(airportRepository.findById(flight.getDestination().getIataCode())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("El aeropuerto de destino no existe.", ex.getMessage());
    }

    @Test
    void whenAirlineNotFoundInDB_thenThrowsException() {
        Flight flight = buildValidFlight();
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));
        when(airportRepository.findById(flight.getDestination().getIataCode())).thenReturn(Optional.of(flight.getDestination()));
        when(airlineRepository.findById(flight.getAirline().getCode())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("La aerolínea no existe.", ex.getMessage());
    }

    @Test
    void whenPlaneNotFoundInDB_thenThrowsException() {
        Flight flight = buildValidFlight();
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));
        when(airportRepository.findById(flight.getDestination().getIataCode())).thenReturn(Optional.of(flight.getDestination()));
        when(airlineRepository.findById(flight.getAirline().getCode())).thenReturn(Optional.of(flight.getAirline()));
        when(airplaneRepository.findById(flight.getPlane().getId())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("El avión no existe.", ex.getMessage());
    }

    @Test
    void whenAllFieldsValid_thenSavesFlight() {
        Flight flight = buildValidFlight();

        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));
        when(airportRepository.findById(flight.getDestination().getIataCode())).thenReturn(Optional.of(flight.getDestination()));
        when(airlineRepository.findById(flight.getAirline().getCode())).thenReturn(Optional.of(flight.getAirline()));
        when(airplaneRepository.findById(flight.getPlane().getId())).thenReturn(Optional.of(flight.getPlane()));
        when(flightRepository.save(any())).thenReturn(flight);

        assertNotNull(flightService.save(flight));
        verify(flightRepository).save(flight);
    }

    @Test
    void whenOriginIsNull_thenThrowsException() {
        Flight flight = buildFlightWithNullOrigin();

        assertThrows(IllegalArgumentException.class, () -> flightService.save(flight));
    }

    @Test
    void whenDestinationIsNull_thenThrowsException() {
        Flight flight = buildFlightWithNullDestination();
        // Origin existis but destination doesn't
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("El aeropuerto de destino no existe.", ex.getMessage());
    }

    @Test
    void whenAirlineIsNull_thenThrowsException() {
        Flight flight = buildFlightWithNullAirline();
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));
        when(airportRepository.findById(flight.getDestination().getIataCode())).thenReturn(Optional.of(flight.getDestination()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("La aerolínea no existe.", ex.getMessage());
    }

    @Test
    void whenPlaneIsNull_thenThrowsException() {
        Flight flight = buildFlightWithNullPlane();
        when(airportRepository.findById(flight.getOrigin().getIataCode())).thenReturn(Optional.of(flight.getOrigin()));
        when(airportRepository.findById(flight.getDestination().getIataCode())).thenReturn(Optional.of(flight.getDestination()));
        when(airlineRepository.findById(flight.getAirline().getCode())).thenReturn(Optional.of(flight.getAirline()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> flightService.save(flight));
        assertEquals("El avión no existe.", ex.getMessage());
    }

    @Test
    void whenFlightExistsByAirlineAndNumber_thenReturnsIt() {
        Flight flight = buildValidFlight();
        when(flightRepository.findByAirlineCodeAndId(flight.getAirline().getCode(), 1L)).thenReturn(Optional.of(flight));

        Optional<Flight> result = flightService.findByAirlineAndNumber(flight.getAirline().getCode(), 1L);

        assertTrue(result.isPresent());
        assertEquals(flight, result.get());
    }

    @Test
    void whenFlightNotExistsByAirlineAndNumber_thenReturnsEmpty() {
        Flight flight = buildValidFlight();
        when(flightRepository.findByAirlineCodeAndId(flight.getAirline().getCode(), 99L)).thenReturn(Optional.empty());

        Optional<Flight> result = flightService.findByAirlineAndNumber(flight.getAirline().getCode(), 99L);

        assertTrue(result.isEmpty());
    }
}
