package integration;

import model.Flight;
import repository.FlightRepository;
import service.FlightService;
import utils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Levanta todo el contexto de Spring y la DB H2
@Transactional   // Hace rollback después de cada test para no ensuciar la DB
class FlightServiceIT {
    private static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");
    private static final ZoneId NEW_YORK_ZONE = ZoneId.of("America/New_York");
    private static final ZoneId LONDON_ZONE = ZoneId.of("Europe/London");

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TestDataFactory dataFactory;

    @Test
    void testFindFlightsByDateInDatabase() {
        Flight f = new Flight();
        f.setEstimatedLocalDepartureTime(LocalDateTime.now());
        flightRepository.save(f);

        String today = LocalDate.now().toString();
        List<Flight> resultsToday = flightService.findByDate(today);

        String tomorrow = LocalDate.now().plusDays(1).toString();
        List<Flight> resultsTomorrow = flightService.findByDate(tomorrow);

        assertFalse(resultsToday.isEmpty(), "Debería encontrar el vuelo de hoy");
        assertTrue(resultsTomorrow.isEmpty(), "No debería encontrar nada para mañana");
    }

    @Test
    @Transactional
    void testFindByDate_ShouldReturnOnlySpecificDay() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime withinFiveDays = today.plusDays(5);

        dataFactory.createFlight(
                "IB", "Iberia",
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", MADRID_ZONE,
                "JFK", "John F. Kennedy International", "New York", NEW_YORK_ZONE,
                today, withinFiveDays.plusHours(8)
        );

        dataFactory.createFlight(
                "UA", "United Airlines",
                "JFK", "John F. Kennedy International", "New York", NEW_YORK_ZONE,
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", MADRID_ZONE,
                withinFiveDays, withinFiveDays.plusHours(8)
        );

        List<Flight> results = flightService.findByDate(withinFiveDays.toLocalDate().toString());

        assertEquals(1, results.size(), "Debería haber exactamente un vuelo");
        assertEquals("UA", results.get(0).getAirline().getCode());
    }

    @Test
    @Transactional
    void testFindByAirline_ShouldWorkWithAnyCode() {
        LocalDateTime departure = LocalDateTime.now();
        Flight saved = dataFactory.createFlight(
                "VY", "Vueling",
                "BCN", "Josep Tarradellas Barcelona-El Prat", "Barcelona", MADRID_ZONE,
                "LGW", "London Gatwick", "London", LONDON_ZONE,
                departure, departure.plusHours(2)
        );

        Optional<Flight> found = flightService.findByAirlineAndNumber("VY", saved.getId());

        assertTrue(found.isPresent());
        assertEquals("BCN", found.get().getOrigin().getIataCode());
        assertEquals("Vueling", found.get().getAirline().getName());
    }

    @Test
    @Transactional
    void testFindByAirlineAndNumber_ShouldReturnCorrectFlight() {
        LocalDateTime now = LocalDateTime.now();

        Flight flightIB = dataFactory.createFlight(
                "IB", "Iberia", "MAD", "Barajas", "Madrid", MADRID_ZONE,
                "LHR", "Heathrow", "London", LONDON_ZONE, now, now.plusHours(2)
        );

        Flight flightUA = dataFactory.createFlight(
                "UA", "United", "EWR", "Newark",
                "New Jersey", NEW_YORK_ZONE, "MAD", "Barajas",
                "Madrid", MADRID_ZONE, now, now.plusHours(7)
        );

        Optional<Flight> found = flightService.findByAirlineAndNumber("IB", flightIB.getId());

        assertTrue(found.isPresent(), "El vuelo de Iberia debería existir");
        assertEquals("IB", found.get().getAirline().getCode());

        Optional<Flight> notFound = flightService.findByAirlineAndNumber("UA", flightIB.getId());
        assertTrue(notFound.isEmpty(), "No debería encontrarlo si el código de aerolínea está mal");
    }
    @Test
    void whenSaveValidFlight_thenPersistsInDB() {
        Flight saved = dataFactory.createValidFlight();

        Optional<Flight> found = flightRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("IB", found.get().getAirline().getCode());
        assertEquals("MAD", found.get().getOrigin().getIataCode());
    }

}
