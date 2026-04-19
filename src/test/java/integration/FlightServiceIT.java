package integration;

import model.CargoFlight;
import model.CommercialFlight;
import model.Flight;
import model.FlightState;
import repository.FlightRepository;
import service.FlightService;
import utils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FlightServiceIT {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    // --- getFlightsListBetweenDates ---

    @Test
    void whenFlightExistsOnDateShouldReturnIt() {
        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        LocalDateTime arrival = departure.plusHours(8);
        CommercialFlight flight = TestDataFactory.buildCommercialFlight(departure, arrival, 100, TestDataFactory.buildPassengers());
        flightRepository.saveFlight(flight);

        LocalDate date = departure.toLocalDate();
        List<Flight> results = flightService.getFlightsListBetweenDates(date, date.plusDays(1));

        assertFalse(results.isEmpty());
        assertTrue(results.contains(flight));
    }

    @Test
    void whenNoFlightsOnDateShouldReturnEmpty() {
        LocalDate pastDate = LocalDate.now().minusDays(10);

        List<Flight> results = flightService.getFlightsListBetweenDates(pastDate, pastDate.plusDays(1));

        assertTrue(results.isEmpty());
    }

    @Test
    void whenFlightsOnDifferentDatesShouldReturnOnlyRequested() {
        LocalDateTime todayDeparture = LocalDateTime.now().plusDays(1);
        LocalDateTime futureDeparture = LocalDateTime.now().plusDays(5);

        CommercialFlight flightToday = TestDataFactory.buildCommercialFlight(
                todayDeparture, todayDeparture.plusHours(8), 100, TestDataFactory.buildPassengers()
        );
        CargoFlight flightFuture = TestDataFactory.buildCargoFlight(
                futureDeparture, futureDeparture.plusHours(8), BigDecimal.valueOf(5000)
        );
        flightRepository.saveFlight(flightToday);
        flightRepository.saveFlight(flightFuture);

        LocalDate from = futureDeparture.toLocalDate();
        LocalDate to = from.plusDays(1);
        List<Flight> results = flightService.getFlightsListBetweenDates(from, to);

        assertEquals(1, results.size());
        assertTrue(results.contains(flightFuture));
    }

    // --- saveFlight / findFlightByNumber ---

    @Test
    void whenSaveValidFlightShouldPersistInDB() {
        CommercialFlight flight = TestDataFactory.buildValidFlight();
        flightRepository.saveFlight(flight);

        Flight found = flightRepository.findFlightByNumber(TestDataFactory.buildAirline(), "FL001");

        assertNotNull(found);
        assertEquals("IB", found.getAirline().getAirlineCode());
        assertEquals("MAD", found.getOrigin().getIataCode());
    }

    // --- updateFlightState ---

    @Test
    void whenUpdateFlightStateShouldPersistNewState() {
        CommercialFlight flight = TestDataFactory.buildValidFlight();
        flightRepository.saveFlight(flight);

        flightService.updateFlightState("FL001", TestDataFactory.buildAirline(), FlightState.DELAYED);

        Flight updated = flightRepository.findFlightByNumber(TestDataFactory.buildAirline(), "FL001");
        assertEquals(FlightState.DELAYED, updated.getFlightState());
    }

    @Test
    void whenUpdateFlightStateWithInvalidFlightShouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                flightService.updateFlightState("XX9999", TestDataFactory.buildAirline(), FlightState.DELAYED)
        );
    }

    // --- getAirlineFlightsQuantity ---

    @Test
    void whenAirlineHasFlightsShouldReturnCorrectCount() {
        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        CommercialFlight flight1 = TestDataFactory.buildCommercialFlight(
                departure, departure.plusHours(8), 100, TestDataFactory.buildPassengers()
        );
        CargoFlight flight2 = TestDataFactory.buildCargoFlight(
                departure.plusDays(1), departure.plusDays(1).plusHours(8), BigDecimal.valueOf(3000)
        );
        flightRepository.saveFlight(flight1);
        flightRepository.saveFlight(flight2);

        int result = flightService.getAirlineFlightsQuantity(TestDataFactory.buildAirline());

        assertEquals(2, result);
    }
}
