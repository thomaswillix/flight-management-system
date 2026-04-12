package unit.model;

import model.Airport;
import model.Flight;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlightTest {

    private static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");
    private static final ZoneId TOKYO_ZONE = ZoneId.of("Asia/Tokyo");

    private Flight flightWithZones(ZoneId departureZone, ZoneId arrivalZone) {
        Airport origin = new Airport();
        origin.setTimeZone(departureZone);

        Airport destination = new Airport();
        destination.setTimeZone(arrivalZone);

        Flight flight = new Flight();
        flight.setOrigin(origin);
        flight.setDestination(destination);
        return flight;
    }

    @Test
    void whenEstimatedTimesAreValid_thenReturnsCorrectDuration() {
        Flight flight = flightWithZones(MADRID_ZONE, TOKYO_ZONE);
        flight.setEstimatedLocalDepartureTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        flight.setEstimatedLocalArrivalTime(LocalDateTime.of(2026, 1, 2, 2, 30));

        assertEquals(510, flight.getEstimatedFlightDurationInMinutes());
    }

    @Test
    void whenRealTimesAreValid_thenReturnsCorrectDuration() {
        Flight flight = flightWithZones(MADRID_ZONE, TOKYO_ZONE);
        flight.setRealLocalDepartureTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        flight.setRealLocalArrivalTime(LocalDateTime.of(2026, 1, 1, 20, 0));

        assertEquals(120, flight.getRealFlightDurationInMinutes());
    }

    @Test
    void whenDepartureIsAfterArrival_thenReturnsZero() {
        Flight flight = flightWithZones(MADRID_ZONE, MADRID_ZONE);
        LocalDateTime now = LocalDateTime.now();
        flight.setEstimatedLocalDepartureTime(now.plusHours(5));
        flight.setEstimatedLocalArrivalTime(now);

        assertEquals(0, flight.getEstimatedFlightDurationInMinutes());
    }

    @Test
    void whenDepartureIsNull_thenReturnsZero() {
        Flight flight = flightWithZones(MADRID_ZONE, MADRID_ZONE);
        flight.setEstimatedLocalDepartureTime(null);
        flight.setEstimatedLocalArrivalTime(LocalDateTime.now());

        assertEquals(0, flight.getEstimatedFlightDurationInMinutes());
    }

    @Test
    void whenArrivalIsNull_thenReturnsZero() {
        Flight flight = flightWithZones(MADRID_ZONE, MADRID_ZONE);
        flight.setEstimatedLocalDepartureTime(LocalDateTime.now());
        flight.setEstimatedLocalArrivalTime(null);

        assertEquals(0, flight.getEstimatedFlightDurationInMinutes());
    }

    @Test
    void whenBothTimesAreNull_thenReturnsZero() {
        Flight flight = flightWithZones(MADRID_ZONE, MADRID_ZONE);

        assertEquals(0, flight.getEstimatedFlightDurationInMinutes());
        assertEquals(0, flight.getRealFlightDurationInMinutes());
    }

    @Test
    void whenDepartureEqualsArrival_thenReturnsZero() {
        Flight flight = flightWithZones(MADRID_ZONE, MADRID_ZONE);
        LocalDateTime now = LocalDateTime.now();
        flight.setEstimatedLocalDepartureTime(now);
        flight.setEstimatedLocalArrivalTime(now);

        assertEquals(0, flight.getEstimatedFlightDurationInMinutes());
    }
}
