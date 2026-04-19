package unit.model;

import model.Passenger;
import utils.TestDataFactory;
import model.CommercialFlight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CommercialFlightTest {
    CommercialFlight commercialFlight;
    private static final LocalDateTime DEPARTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime ARRIVAL = DEPARTURE.plusHours(8);

    @Test
    public void whenFieldsAreValidShouldCreateCargoFlight(){
        Integer capacity = 100;
        Set<Passenger> passengers = TestDataFactory.buildPassengers();
        assertDoesNotThrow( () ->
                commercialFlight = TestDataFactory.buildCommercialFlight(
                        DEPARTURE, ARRIVAL, capacity, passengers
                )
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    public void whenCargoWeightIsInvalidShouldThrowException(Integer invalidCapacities) {
        Set<Passenger> passengers = TestDataFactory.buildPassengers();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commercialFlight = TestDataFactory.buildCommercialFlight(
                        DEPARTURE, ARRIVAL, invalidCapacities, passengers
                )
        );
        assertEquals("Capacity cannot be 0 or negative", exception.getMessage());
    }

    @Test
    public void whenPassengerListIsEmptyShouldThrowException() {
        Integer capacity = 100;
        Set<Passenger> emptyPassengersList = new HashSet<>();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commercialFlight = TestDataFactory.buildCommercialFlight(
                        DEPARTURE, ARRIVAL, capacity, emptyPassengersList
                )
        );
        assertEquals("Passengers list cannot be null or empty", exception.getMessage());
    }

    @Test
    public void whenPassengerListIsNullShouldThrowException() {
        Integer capacity = 100;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commercialFlight = TestDataFactory.buildCommercialFlight(
                        DEPARTURE, ARRIVAL, capacity, null
                )
        );
        assertEquals("Passengers list cannot be null or empty", exception.getMessage());
    }

    @Test
    public void whenPassengerListExceedsCapacityShouldThrowException() {
        Integer capacity = 1;
        Set<Passenger> passengers = TestDataFactory.buildPassengers();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commercialFlight = TestDataFactory.buildCommercialFlight(
                        DEPARTURE, ARRIVAL, capacity, passengers
                )
        );
        assertEquals("Passengers cannot exceed flight capacity", exception.getMessage());
    }

    @Test
    public void estimatedCommercialFlightWeightIsCalculatedCorrectly(){
        Integer capacity = 100;
        Set<Passenger> passengers = TestDataFactory.buildPassengers();
        commercialFlight = TestDataFactory.buildCommercialFlight(DEPARTURE, ARRIVAL, capacity, passengers);
        BigDecimal expectedEstimatedFlightWeight = BigDecimal.valueOf(commercialFlight.getPassengers().size() *
                CommercialFlight.AVERAGE_PERSON_WEIGHT_KG
        );
        assertEquals(expectedEstimatedFlightWeight, commercialFlight.getEstimatedFlightWeight());
    }
}
