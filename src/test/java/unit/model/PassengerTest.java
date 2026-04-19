package unit.model;

import model.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerTest {

    private Passenger passenger;

    @Test
    public void whenFieldsAreValidWithPhoneShouldCreatePassenger() {
        assertDoesNotThrow(() ->
                passenger = new Passenger("76797579G", "Felipe", "Pérez", "612345678")
        );
    }

    @Test
    public void whenFieldsAreValidWithEmailAndPhoneShouldCreatePassenger() {
        assertDoesNotThrow(() ->
                passenger = new Passenger("76797579G", "Felipe", "Pérez", "felipe@email.com", "612345678")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1234", "ABC12345Z", "12345678"})
    public void whenNationalIdIsInvalidShouldThrowException(String invalidDni) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                passenger = new Passenger(invalidDni, "Felipe", "Pérez", "612345678")
        );
        assertEquals("Invalid Spanish DNI format or checksum: " + invalidDni, ex.getMessage());
    }

    @Test
    public void whenNameIsEmptyShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                passenger = new Passenger("76797579G", "", "Pérez", "612345678")
        );
        assertEquals("Name can't be blank", ex.getMessage());
    }

    @Test
    public void whenNameIsNullShouldThrowException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                passenger = new Passenger("76797579G", null, "Pérez", "612345678")
        );
        assertEquals("Name can't be null", ex.getMessage());
    }

    @Test
    public void whenSurnameIsEmptyShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                passenger = new Passenger("76797579G", "Felipe", "", "612345678")
        );
        assertEquals("Surname can't be blank", ex.getMessage());
    }

    @Test
    public void whenSurnameIsNullShouldThrowException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                passenger = new Passenger("76797579G", "Felipe", null, "612345678")
        );
        assertEquals("Surname can't be null", ex.getMessage());
    }

    @Test
    public void whenPhoneIsValidShouldCreatePassenger() {
        assertDoesNotThrow(() ->
                passenger = new Passenger("76797579G", "Felipe", "Pérez", "612345678")
        );
        assertFalse(passenger.sendEmailNotifications);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123456789", "512345678", "61234567", "6123456789"})
    public void whenPhoneIsInvalidShouldThrowException(String invalidPhone) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                passenger = new Passenger("76797579G", "Felipe", "Pérez", invalidPhone)
        );
        assertEquals("Invalid Spanish phone number", ex.getMessage());
    }

    @Test
    public void whenEmailIsValidShouldEnableEmailNotifications() {
        passenger = new Passenger("76797579G", "Felipe", "Pérez", "felipe@email.com", "612345678");
        assertTrue(passenger.sendEmailNotifications);
    }

    @ParameterizedTest
    @ValueSource(strings = {"noesuncorreo", "sin@dominio", "@sinusuario.com", "espacios @email.com"})
    public void whenEmailIsInvalidShouldThrowException(String invalidEmail) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                passenger = new Passenger("76797579G", "Felipe", "Pérez", invalidEmail, "612345678")
        );
        assertEquals("Email format is not valid", ex.getMessage());
    }

    @Test
    public void whenNoEmailShouldDefaultToSmsNotifications() {
        passenger = new Passenger("76797579G", "Felipe", "Pérez", "612345678");
        assertFalse(passenger.sendEmailNotifications);
    }

    @Test
    public void whenNationalIdIsTheSameShouldResolveEqual() {
        passenger = new Passenger("76797579G", "Felipe", "Pérez", "612345678");
        Passenger secondPassenger = new Passenger("76797579G", "Juan", "Hernández", "698765432");
        assertEquals(passenger, secondPassenger);
    }

    @Test
    public void whenNationalIdIsNotTheSameShouldNotResolveEqual() {
        passenger = new Passenger("76797579G", "Felipe", "Pérez", "612345678");
        Passenger secondPassenger = new Passenger("26641656C", "Juan", "Hernández", "698765432");
        assertNotEquals(passenger, secondPassenger);
    }
}
