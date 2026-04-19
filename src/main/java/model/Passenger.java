package model;

import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

import static utils.Validation.requireNotBlank;

@Entity
@Getter
public class Passenger {
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}[A-Z]$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[679]\\d{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    private final String nationalId;
    private final String name;
    private final String surname;
    private final String email;
    private final String phone;
    public final boolean sendEmailNotifications;

    // Email is not necessary but if the user wants notifications to reach his email they will.
    public Passenger(String nationalId, String name, String surname, String email, String phone) {
        this.nationalId = requireValidNationalId(nationalId);
        this.name = requireNotBlank(name, "Name");
        this.surname = requireNotBlank(surname, "Surname");
        this.email = requireValidEmail(email);
        this.phone = requireValidPhone(phone);
        this.sendEmailNotifications = true;
    }

    // Phone is required, all passenger's notifications will reach their phone number by default via sms.
    public Passenger(String nationalId, String name, String surname, String phone) {
        this.nationalId = requireValidNationalId(nationalId);
        this.name = requireNotBlank(name, "Name");
        this.surname = requireNotBlank(surname, "Surname");
        this.email = null;
        this.phone = requireValidPhone(phone);
        this.sendEmailNotifications = false;
    }

    public String requireValidNationalId(String nationalId) {
        if (!isValidSpanishDni(nationalId))
            throw new IllegalArgumentException("Invalid Spanish DNI format or checksum: " + nationalId);

        return nationalId;
    }

    private static boolean isValidSpanishDni(String dni) {
        if (dni == null || !DNI_PATTERN.matcher(dni).matches()) return false;

        int number = Integer.parseInt(dni.substring(0, 8));
        if (dni.charAt(8) != DNI_LETTERS.charAt(number % 23)) return false;

        return true;
    }

    private static String requireValidPhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches())
            throw new IllegalArgumentException("Invalid Spanish phone number");
        return phone;
    }

    private static String requireValidEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches())
            throw new IllegalArgumentException("Email format is not valid");
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(nationalId, passenger.nationalId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nationalId);
    }
}
