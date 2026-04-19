package unit.model;

import model.Airline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class AirlineTest {

    private Airline airline;

    @Test
    public void whenFieldsAreValidShouldCreateAirline(){
        assertDoesNotThrow( () -> airline = new Airline( "IB", "Iberia"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "A", "<Z", "IBE", "1A", "12"})
    public void whenAirlineCodeIsInvalidShouldThrowException(String invalidAirlineCode){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                airline = new Airline( invalidAirlineCode, "Iberia")
        );
        assertEquals("Airline code must be exactly 2 uppercase letters (e.g. IB, UA)", ex.getMessage());
    }

    @Test
    public void whenAirlineCodeIsNullShouldThrowException(){
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                airline = new Airline( null, "Iberia")
        );
        assertEquals("Airline code can't be null", ex.getMessage());
    }

    @Test
    public void whenAirlineCodeIsEmptyShouldThrowException(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                airline = new Airline( "", "Iberia")
        );
        assertEquals("Airline code can't be blank", ex.getMessage());
    }

    @Test
    public void whenAirlineNameIsNullShouldThrowException(){
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                airline = new Airline( "IB", null)
        );
        assertEquals("Airline name can't be null", ex.getMessage());
    }

    @Test
    public void whenAirlineNameIsEmptyShouldThrowException(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                airline = new Airline( "IB", "")
        );
        assertEquals("Airline name can't be blank", ex.getMessage());
    }

    @Test
    public void whenAirlineCodeIsTheSameShouldResolveEqual(){
        airline = new Airline("IB", "Iberia");
        Airline secondAirline = new Airline("IB", "Test");
        assertEquals(airline, secondAirline);
    }

    @Test
    public void whenAirlineCodeIsNotTheSameShouldNotResolveEqual(){
        airline = new Airline("IB", "Iberia");
        Airline secondAirline = new Airline("UA", "Iberia");
        assertNotEquals(airline, secondAirline);
    }
}
