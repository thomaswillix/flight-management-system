package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.regex.Pattern;

import static utils.Validation.requireNotBlank;

@Entity
@Getter
@Setter
public class Airline {

    private static final Pattern AIRLINE_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, unique = true, nullable = false)
    private String airlineCode;
    private String name;

    public Airline() {}

    public Airline(String airlineCode, String name) {
        this.airlineCode = requireValidAirlineCode(airlineCode);
        this.name = requireNotBlank(name, "Airline name");
    }

    public static String requireValidAirlineCode(String airlineCode) {
        requireNotBlank(airlineCode, "Airline code");
        if (!AIRLINE_CODE_PATTERN.matcher(airlineCode).matches())
            throw new IllegalArgumentException("Airline code must be exactly 2 uppercase letters (e.g. IB, UA)");
        return airlineCode;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "airlineCode='" + airlineCode + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Airline airline = (Airline) o;
        return Objects.equals(airlineCode, airline.airlineCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(airlineCode);
    }
}
