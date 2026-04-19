package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.util.Objects;
import java.util.regex.Pattern;

import static utils.Validation.requireNotBlank;
import static utils.Validation.requireNotNull;

@Entity
@Getter
@Setter
public class Airport {
    private static final Pattern IATA_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    @Id
    @Column(length = 3)
    private String iataCode;
    private String airportName;
    private String city;
    private ZoneId timeZone;

    public Airport() {}

    public Airport(String iataCode, String airportName, String city, ZoneId timeZone) {
        this.iataCode = requireValidIataCode(iataCode);
        this.airportName = requireNotBlank(airportName, "Airport name");
        this.city = requireNotBlank(city, "City");
        this.timeZone = requireNotNull(timeZone, "Time zone");
    }

    private String requireValidIataCode(String iataCode){
        requireNotBlank(iataCode, "IATA code");
        if (!IATA_CODE_PATTERN.matcher(iataCode).matches())
            throw new IllegalArgumentException("IATA code must be exactly 3 uppercase letters (e.g. MAD, JFK)");
        return iataCode;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "iataCode='" + iataCode + '\'' +
                ", airportName='" + airportName + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return Objects.equals(iataCode, airport.iataCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(iataCode);
    }
}
