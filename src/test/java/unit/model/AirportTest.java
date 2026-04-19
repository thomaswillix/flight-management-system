package unit.model;

import model.Airport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static utils.TestDataFactory.MADRID_ZONE;
import static utils.TestDataFactory.NEW_YORK_ZONE;
import static org.junit.jupiter.api.Assertions.*;

public class AirportTest {

    private Airport airport;

    @Test
    public void whenFieldsAreValidShouldCreateAirport(){
        assertDoesNotThrow( () -> airport = new Airport(
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", MADRID_ZONE
                )
        );
    }
    @ParameterizedTest
    @ValueSource(strings = {"MADRID", "M", "<Z", "MD", "M4", "12"})
    public void whenIataCodeIsInvalidShouldThrowException(String invalidIataCode){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> airport = new Airport(
                invalidIataCode, "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", MADRID_ZONE
                )
        );
        assertEquals("IATA code must be exactly 3 uppercase letters (e.g. MAD, JFK)", ex.getMessage());
    }

    @Test
    public void whenIataCodeIsNullShouldThrowException(){
        NullPointerException ex = assertThrows(NullPointerException.class, () -> airport = new Airport(
                null, "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", MADRID_ZONE
                )
        );
        assertEquals("IATA code can't be null", ex.getMessage());
    }

    @Test
    public void whenIataCodeIsEmptyShouldThrowException(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> airport = new Airport(
                "", "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", MADRID_ZONE
                )
        );
        assertEquals("IATA code can't be blank", ex.getMessage());
    }

    @Test
    public void whenAirportNameIsNullShouldThrowException(){
        NullPointerException ex = assertThrows(NullPointerException.class, () -> airport = new Airport(
                "MAD", null, "Madrid/Spain", MADRID_ZONE
                )
        );
        assertEquals("Airport name can't be null", ex.getMessage());
    }

    @Test
    public void whenAirportNameIsEmptyShouldThrowException(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> airport = new Airport(
                "MAD", "", "Madrid/Spain", MADRID_ZONE
                )
        );
        assertEquals("Airport name can't be blank", ex.getMessage());
    }

    @Test
    public void whenAirportCityIsNullShouldThrowException(){
        NullPointerException ex = assertThrows(NullPointerException.class, () -> airport = new Airport(
                "MAD", "Adolfo Suárez Madrid-Barajas", null, MADRID_ZONE
                )
        );
        assertEquals("City can't be null", ex.getMessage());
    }

    @Test
    public void whenAirportCityIsEmptyShouldThrowException(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> airport = new Airport(
                "MAD", "Adolfo Suárez Madrid-Barajas", "", MADRID_ZONE
                )
        );
        assertEquals("City can't be blank", ex.getMessage());
    }

    @Test
    public void whenIataCodeIsTheSameShouldResolveEqual(){
        airport = new Airport(
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", MADRID_ZONE
        );
        Airport secondAirport = new Airport(
                "MAD", "John F. Kennedy International", "New York/United States", NEW_YORK_ZONE
        );
        assertEquals(airport, secondAirport);
    }

    @Test
    public void whenIataCodeIsNotTheSameShouldNotResolveEqual(){
        airport = new Airport(
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", MADRID_ZONE
        );
        Airport secondAirport = new Airport(
                "JFK", "John F. Kennedy International", "New York/United States", NEW_YORK_ZONE
        );
        assertNotEquals(airport, secondAirport);
    }

    @Test
    public void whenTimeZoneIsNullShouldThrowException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> airport = new Airport(
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid/Spain", null
        ));
        assertEquals("Time zone can't be null", ex.getMessage());
    }
}
