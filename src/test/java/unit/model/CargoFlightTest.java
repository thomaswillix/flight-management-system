package unit.model;

import utils.TestDataFactory;
import model.CargoFlight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CargoFlightTest {
    private static CargoFlight cargoFlight;
    private static final LocalDateTime DEPARTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime ARRIVAL = DEPARTURE.plusHours(8);
    private static final BigDecimal CARGO_WEIGHT = BigDecimal.valueOf(20000);

    @Test
    public void whenFieldsAreValidShouldCreateCargoFlight(){
        assertDoesNotThrow( () -> cargoFlight = TestDataFactory.buildCargoFlight(DEPARTURE, ARRIVAL, CARGO_WEIGHT));
    }

    static Stream<BigDecimal> invalidWeights() {
        return Stream.of(
                BigDecimal.ZERO,
                new BigDecimal("-1"),
                new BigDecimal("-100"),
                new BigDecimal("-0.01")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidWeights")
    public void whenCargoWeightIsInvalidShouldThrowException(BigDecimal invalidWeight) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cargoFlight = TestDataFactory.buildCargoFlight(DEPARTURE, ARRIVAL, invalidWeight)
        );
        assertEquals("Cargo weight must be greater than 0", exception.getMessage());
    }

    @Test
    public void estimatedCargoFlightWeightIsCalculatedCorrectly(){
        cargoFlight = TestDataFactory.buildCargoFlight(DEPARTURE, ARRIVAL, CARGO_WEIGHT);
        BigDecimal expectedEstimatedFlightWeight = CARGO_WEIGHT.add(cargoFlight.getAirplane().getAirplaneWeight());
        assertEquals(expectedEstimatedFlightWeight, cargoFlight.getEstimatedFlightWeight());
    }
}
