package unit.model;

import model.CommercialFlight;
import model.Flight;
import model.FlightTimeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.TestDataFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FlightTest {

    private static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");
    private static final ZoneId TOKYO_ZONE = ZoneId.of("Asia/Tokyo");

    Flight flight;

    @ParameterizedTest
    @MethodSource("invalidDateCases")
    public void whenDatesAreInvalidShouldThrowException(FlightTimeType type, LocalDateTime departure,
                                                        LocalDateTime arrival
    ) {
        flight = TestDataFactory.buildValidFlight();
        String expectedMessage = type + " departure must be before arrival";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            switch (type) {
                case ESTIMATED -> flight.setEstimatedTimes(departure, arrival);
                case REAL      -> flight.setRealTimes(departure, arrival);
            }
        });
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("nullDateCases")
    public void whenDatesAreNullShouldThrowException(FlightTimeType type, LocalDateTime departure,
                                                     LocalDateTime arrival, String expectedMessage) {
        flight = TestDataFactory.buildValidFlight();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            if (type == FlightTimeType.ESTIMATED) {
                flight.setEstimatedTimes(departure, arrival);
            }
        });
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void whenAirlineIsNullShouldThrowException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flight = TestDataFactory.buildFlightWithNullAirline()
        );
        assertEquals("Airline can't be null", exception.getMessage());
    }

    @Test
    public void whenAirplaneIsNullShouldThrowException(){
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flight = TestDataFactory.buildFlightWithNullAirplane()
        );
        assertEquals("Airplane can't be null", exception.getMessage());
    }

    @Test
    public void whenOriginAirportIsNullShouldThrowException(){
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flight = TestDataFactory.buildFlightWithNullOrigin()
        );
        assertEquals("Origin airport can't be null", exception.getMessage());
    }

    @Test
    public void whenDestinationAirportIsNullShouldThrowException(){
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                flight = TestDataFactory.buildFlightWithNullDestination()
        );
        assertEquals("Destination airport can't be null", exception.getMessage());
    }

    // --- getFlightDurationInTimeUnit ---

    @ParameterizedTest
    @MethodSource("flightDurationCases")
    public void flightDurationShouldBeCalculatedCorrectly(FlightTimeType type, ChronoUnit unit) {
        CommercialFlight flight = TestDataFactory.buildValidFlight();
        LocalDateTime realDeparture = LocalDateTime.now().plusDays(1);
        flight.setRealTimes(realDeparture, realDeparture.plusHours(8));

        ZoneId originZone = flight.getOrigin().getTimeZone();
        ZoneId destinationZone = flight.getDestination().getTimeZone();

        LocalDateTime departure = switch (type) {
            case PLANNED   -> flight.getPlannedLocalDepartureTime();
            case ESTIMATED -> flight.getEstimatedLocalDepartureTime();
            case REAL      -> flight.getRealLocalDepartureTime();
        };
        LocalDateTime arrival = switch (type) {
            case PLANNED   -> flight.getPlannedLocalArrivalTime();
            case ESTIMATED -> flight.getEstimatedLocalArrivalTime();
            case REAL      -> flight.getRealLocalArrivalTime();
        };

        long expected = unit.between(departure.atZone(originZone), arrival.atZone(destinationZone));

        assertEquals(expected, flight.getFlightDurationInTimeUnit(type, unit));
    }

    @Test
    public void whenRealTimesAreNullShouldThrowIllegalStateException() {
        CommercialFlight flight = TestDataFactory.buildValidFlight();
        // realDeparture realArrival are null

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                flight.getFlightDurationInTimeUnit(FlightTimeType.REAL, ChronoUnit.MINUTES)
        );
        assertEquals("Flight times not available for type: REAL", exception.getMessage());
    }

    @Test
    public void whenTimeUnitIsNullShouldThrowIllegalArgumentException() {
        CommercialFlight flight = TestDataFactory.buildValidFlight();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flight.getFlightDurationInTimeUnit(FlightTimeType.PLANNED, null)
        );
        assertEquals("Time unit can't be null", exception.getMessage());
    }

    @Test
    public void whenFlightTimeTypeIsNullShouldThrowException() {
        CommercialFlight flight = TestDataFactory.buildValidFlight();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flight.getFlightDurationInTimeUnit(null, ChronoUnit.MINUTES)
        );
        assertEquals("Flight time type can't be null", exception.getMessage());
    }

    // --- arrivesSameDay ---

    @Test
    public void whenFlightDepartsAndArrivesAtTheSameDay_arrivesSameDayShouldReturnTrue() {
        CommercialFlight validFlight = TestDataFactory.buildValidFlight();
        LocalDateTime realDeparture = LocalDateTime.now().plusDays(1).toLocalDate().atTime(8, 0);
        validFlight.setRealTimes(realDeparture, realDeparture.plusHours(4));
        assertTrue(validFlight.arrivesSameDay());
    }

    @Test
    public void whenFlightDepartsAndArrivesOnDifferentDays_arrivesSameDayShouldReturnFalse() {
        CommercialFlight validFlight = TestDataFactory.buildValidFlight();
        LocalDateTime realDeparture = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
        validFlight.setRealTimes(realDeparture, realDeparture.plusHours(25));
        assertFalse(validFlight.arrivesSameDay());
    }

    //Private helper methods

    private static Stream<Arguments> flightDurationCases() {
        return Stream.of(
                Arguments.of(FlightTimeType.PLANNED,   ChronoUnit.HOURS),
                Arguments.of(FlightTimeType.PLANNED,   ChronoUnit.MINUTES),
                Arguments.of(FlightTimeType.PLANNED,   ChronoUnit.SECONDS),
                Arguments.of(FlightTimeType.ESTIMATED, ChronoUnit.HOURS),
                Arguments.of(FlightTimeType.ESTIMATED, ChronoUnit.MINUTES),
                Arguments.of(FlightTimeType.ESTIMATED, ChronoUnit.SECONDS),
                Arguments.of(FlightTimeType.REAL,      ChronoUnit.HOURS),
                Arguments.of(FlightTimeType.REAL,      ChronoUnit.MINUTES),
                Arguments.of(FlightTimeType.REAL,      ChronoUnit.SECONDS)
        );
    }

    private static Stream<Arguments> invalidDateCases() {
        LocalDateTime arrival = LocalDateTime.now().plusDays(1);
        LocalDateTime departure = arrival.plusHours(8);

        return Stream.of(
                Arguments.of(FlightTimeType.ESTIMATED, departure, arrival),
                Arguments.of(FlightTimeType.REAL,      departure, arrival)
        );
    }

    private static Stream<Arguments> nullDateCases() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        return Stream.of(
                Arguments.of(FlightTimeType.ESTIMATED, null, date,
                        FlightTimeType.ESTIMATED + " departure date can't be null"
                ),
                Arguments.of(FlightTimeType.ESTIMATED, date, null,
                        FlightTimeType.ESTIMATED + " arrival date can't be null"
                )
        );
    }
}
