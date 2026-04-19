package unit.service;

import model.*;
import notification.FlightStateListener;
import org.junit.jupiter.api.BeforeEach;
import repository.AirlineRepository;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;
import service.FlightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.TestDataFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    private FlightRepository flightRepository;
    private AirlineRepository airlineRepository;
    private AirportRepository airportRepository;
    private AirplaneRepository airplaneRepository;

    private FlightStateListener emailListener;
    private FlightStateListener smsListener;

    private FlightService flightService;

    private Airline airline;
    private CommercialFlight commercialFlight;
    private CargoFlight cargoFlight;

    @BeforeEach
    void setUp() {
        airline = TestDataFactory.buildAirline();
        flightRepository = mock(FlightRepository.class);
        airlineRepository = mock(AirlineRepository.class);
        emailListener = mock(FlightStateListener.class);
        smsListener = mock(FlightStateListener.class);

        flightService = new FlightService(flightRepository,List.of(emailListener, smsListener),
                airportRepository, airlineRepository,airplaneRepository);

        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        LocalDateTime arrival = departure.plusHours(8);

        commercialFlight = TestDataFactory.buildCommercialFlight(
                departure, arrival, 100, TestDataFactory.buildPassengers()
        );
        cargoFlight = TestDataFactory.buildCargoFlight(
                departure, arrival, BigDecimal.valueOf(5000)
        );
    }

    // --- getAirlineFlightsQuantity ---

    @Test
    public void whenAirlineIsValidShouldReturnFlightsQuantity() {
        when(flightRepository.findAllFlightsByAirline(airline.getAirlineCode()))
                .thenReturn(List.of(commercialFlight, cargoFlight));

        int result = flightService.getAirlineFlightsQuantity(airline);

        assertEquals(2, result);
    }

    @Test
    public void whenAirlineIsNullOnGetQuantityShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flightService.getAirlineFlightsQuantity(null)
        );
        assertEquals("Airline can't be null", exception.getMessage());
    }

    // --- getPassengersList ---

    @Test
    public void whenFlightIsCommercialShouldReturnPassengersList() {
        when(flightRepository.findFlightByNumber(airline, "FL001"))
                .thenReturn(commercialFlight);

        List<Passenger> result = flightService.getPassengersList("FL001", airline);

        assertEquals(TestDataFactory.buildPassengers().size(), result.size());
    }

    @Test
    public void whenFlightIsCargoShouldThrowUnsupportedOperationException() {
        when(flightRepository.findFlightByNumber(airline, "FL001"))
                .thenReturn(cargoFlight);

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () ->
                flightService.getPassengersList("FL001", airline)
        );
        assertEquals("Cargo flights do not have passengers", exception.getMessage());
    }

    @Test
    public void whenFlightIsNotFoundShouldThrowIllegalArgumentException() {
        when(flightRepository.findFlightByNumber(airline, "XX9999"))
                .thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                flightService.getPassengersList("XX9999", airline)
        );
        assertEquals("Flight was not found", ex.getMessage());
    }

    @Test
    public void whenFlightNumberIsNullShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flightService.getPassengersList(null, airline)
        );
        assertEquals("Flight number can't be null", exception.getMessage());
    }

    @Test
    public void whenFlightNumberIsBlankShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flightService.getPassengersList("", airline)
        );
        assertEquals("Flight number can't be blank", exception.getMessage());
    }

    @Test
    public void whenAirlineIsNullOnGetPassengersShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flightService.getPassengersList("FL001", null)
        );
        assertEquals("Airline can't be null", exception.getMessage());
    }

    // --- getFlightsListBetweenDates ---

    @Test
    public void whenDatesAreValidShouldReturnFlightsBetweenDates() {
        LocalDate departure = LocalDate.of(2024, 1, 1);
        LocalDate arrival = LocalDate.of(2024, 1, 3);
        when(flightRepository.findFlightsByDate(LocalDate.of(2024, 1, 1)))
                .thenReturn(List.of(commercialFlight));
        when(flightRepository.findFlightsByDate(LocalDate.of(2024, 1, 2)))
                .thenReturn(List.of(cargoFlight));

        List<Flight> result = flightService.getFlightsListBetweenDates(departure, arrival);

        assertEquals(2, result.size());
    }

    @Test
    public void whenDepartureDateIsNullShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flightService.getFlightsListBetweenDates(null, LocalDate.of(2024, 1, 3))
        );
        assertEquals("Departure date can't be null", exception.getMessage());
    }

    @Test
    public void whenArrivalDateIsNullShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flightService.getFlightsListBetweenDates(LocalDate.of(2024, 1, 1), null)
        );
        assertEquals("Arrival date can't be null", exception.getMessage());
    }

    // --- getFlightsQuantityPerAirline ---

    @Test
    public void whenAirlinesExistShouldReturnMapWithFlightsPerAirline() {
        Airline secondAirline = new Airline("UA", "United");
        when(airlineRepository.findAllAirlines()).thenReturn(List.of(airline, secondAirline));
        when(flightRepository.findAllFlightsByAirline(airline.getAirlineCode()))
                .thenReturn(List.of(commercialFlight));
        when(flightRepository.findAllFlightsByAirline(secondAirline.getAirlineCode()))
                .thenReturn(List.of(cargoFlight, commercialFlight));

        Map<Airline, Integer> result = flightService.getFlightsQuantityPerAirline();

        assertEquals(1, result.get(airline));
        assertEquals(2, result.get(secondAirline));
    }

    @Test
    public void whenNoAirlinesExistShouldReturnEmptyMap() {
        when(airlineRepository.findAllAirlines()).thenReturn(List.of());

        Map<Airline, Integer> result = flightService.getFlightsQuantityPerAirline();

        assertTrue(result.isEmpty());
    }

    // --- getAverageFlightWeightByAirlineCode ---

    @Test
    public void whenAirlineCodeIsValidShouldReturnAverageWeight() {
        CargoFlight secondCargoFlight = TestDataFactory.buildCargoFlight(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(8),
                BigDecimal.valueOf(3000)
        );
        when(airlineRepository.findAirlineByCode("IB")).thenReturn(airline);
        when(flightRepository.findAllFlightsByAirline(airline.getName()))
                .thenReturn(List.of(cargoFlight, secondCargoFlight));

        BigDecimal airplaneWeight = TestDataFactory.buildAirplane().getAirplaneWeight(); // 40000
        BigDecimal expected = airplaneWeight.add(BigDecimal.valueOf(5000))   // 45000
                .add(airplaneWeight.add(BigDecimal.valueOf(3000)))           // + 43000
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);        // = 44000

        BigDecimal result = flightService.getAverageFlightWeightByAirlineCode("IB");

        assertEquals(expected, result);
    }

    @Test
    public void whenAirlineCodeIsNullOnGetAverageShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flightService.getAverageFlightWeightByAirlineCode(null)
        );
        assertEquals("Airline code can't be null", exception.getMessage());
    }

    @Test
    public void whenAirlineCodeIsInvalidOnGetAverageShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flightService.getAverageFlightWeightByAirlineCode("INVALID")
        );
        assertEquals("Airline code must be exactly 2 uppercase letters (e.g. IB, UA)", exception.getMessage());
    }

    // --- getAllCommercialFlightsFromAirline ---

    @Test
    public void whenAirlineHasCommercialFlightsShouldReturnOnlyCommercialFlights() {
        when(airlineRepository.findAirlineByCode("IB")).thenReturn(airline);
        when(flightRepository.findAllFlightsByAirline(airline.getName()))
                .thenReturn(List.of(commercialFlight, cargoFlight));

        List<Flight> result = flightService.getAllCommercialFlightsFromAirline("IB");

        assertEquals(1, result.size());
        assertInstanceOf(CommercialFlight.class, result.get(0));
    }

    @Test
    public void whenAirlineHasNoCommercialFlightsShouldReturnEmptyList() {
        when(airlineRepository.findAirlineByCode("IB")).thenReturn(airline);
        when(flightRepository.findAllFlightsByAirline(airline.getName()))
                .thenReturn(List.of(cargoFlight));

        List<Flight> result = flightService.getAllCommercialFlightsFromAirline("IB");

        assertTrue(result.isEmpty());
    }

    // --- getAllCargoFlightsFromAirline ---

    @Test
    public void whenAirlineHasCargoFlightsShouldReturnOnlyCargoFlights() {
        when(airlineRepository.findAirlineByCode("IB")).thenReturn(airline);
        when(flightRepository.findAllFlightsByAirline(airline.getName()))
                .thenReturn(List.of(commercialFlight, cargoFlight));

        List<Flight> result = flightService.getAllCargoFlightsFromAirline("IB");

        assertEquals(1, result.size());
        assertInstanceOf(CargoFlight.class, result.get(0));
    }

    @Test
    public void whenAirlineHasNoCargoFlightsShouldReturnEmptyList() {
        when(airlineRepository.findAirlineByCode("IB")).thenReturn(airline);
        when(flightRepository.findAllFlightsByAirline(airline.getName()))
                .thenReturn(List.of(commercialFlight));

        List<Flight> result = flightService.getAllCargoFlightsFromAirline("IB");

        assertTrue(result.isEmpty());
    }

    // --- updateFlightState ---

    @Test
    public void whenFlightExistsAndStateIsValidShouldUpdateState() {
        when(flightRepository.findFlightByNumber(airline, "FL001"))
                .thenReturn(commercialFlight);

        flightService.updateFlightState("FL001", airline, FlightState.DELAYED);

        assertEquals(FlightState.DELAYED, commercialFlight.getFlightState());
        verify(emailListener).onStateChange(commercialFlight);
        verify(smsListener).onStateChange(commercialFlight);
    }

    @Test
    public void whenFlightIsCargoShouldUpdateStateAndNotify() {
        when(flightRepository.findFlightByNumber(airline, "FL001"))
                .thenReturn(cargoFlight);

        flightService.updateFlightState("FL001", airline, FlightState.DELAYED);

        assertEquals(FlightState.DELAYED, cargoFlight.getFlightState());
        verify(emailListener).onStateChange(cargoFlight);
        verify(smsListener).onStateChange(cargoFlight);
    }

    @Test
    public void whenFlightIsNotFoundOnUpdateShouldThrowException() {
        when(flightRepository.findFlightByNumber(airline, "XX9999"))
                .thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                flightService.updateFlightState("XX9999", airline, FlightState.DELAYED)
        );
        assertEquals("Flight was not found", ex.getMessage());
        verify(emailListener, never()).onStateChange(any());
        verify(smsListener, never()).onStateChange(any());
    }

    @Test
    public void whenStateIsNullOnUpdateShouldThrowException() {
        when(flightRepository.findFlightByNumber(airline, "FL001"))
                .thenReturn(commercialFlight);

        assertThrows(NullPointerException.class, () ->
                flightService.updateFlightState("FL001", airline, null)
        );
        verify(emailListener, never()).onStateChange(any());
        verify(smsListener, never()).onStateChange(any());
    }
}
