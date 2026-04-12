package utils;

import model.Airline;
import model.Airplane;
import model.Airport;
import model.Flight;
import repository.AirlineRepository;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TestDataFactory {

    // INSTANCIA: para tests de integración, persiste en H2

    @Autowired private FlightRepository flightRepository;
    @Autowired private AirlineRepository airlineRepository;
    @Autowired private AirportRepository airportRepository;
    @Autowired private AirplaneRepository airplaneRepository;

    public Flight createFlight(
            String airlineCode, String airlineName,
            String originIata, String originName, String originCity, ZoneId originTimeZone,
            String destIata, String destName, String destCity, ZoneId destinationTimeZone,
            LocalDateTime departure, LocalDateTime arrival
    ) {
        Airline airline = airlineRepository.save(buildAirline(airlineCode, airlineName));
        Airport origin = airportRepository.save(buildAirport(originIata, originName, originCity, originTimeZone));
        Airport dest = airportRepository.save(buildAirport(destIata, destName, destCity, destinationTimeZone));
        Airplane plane = airplaneRepository.save(buildAirplane());

        Flight f = new Flight();
        f.setAirline(airline);
        f.setOrigin(origin);
        f.setDestination(dest);
        f.setPlane(plane);
        f.setEstimatedLocalDepartureTime(departure);
        f.setEstimatedLocalArrivalTime(arrival);

        return flightRepository.save(f);
    }

    public Flight createValidFlight() {
        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZoneId newYorkZone = ZoneId.of("US/Eastern");
        return createFlight(
                "IB", "Iberia",
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", madridZone,
                "JFK", "John F. Kennedy International", "New York", newYorkZone,
                departure, departure.plusHours(8)
        );
    }

    // ESTÁTICOS: para tests unitarios, sin BD

    public static Airline buildAirline(String code, String name) {
        Airline a = new Airline();
        a.setCode(code);
        a.setName(name);
        return a;
    }

    public static Airport buildAirport(String iata, String name, String city, ZoneId zone) {
        Airport a = new Airport();
        a.setIataCode(iata);
        a.setAirportName(name);
        a.setCity(city);
        a.setTimeZone(zone);
        return a;
    }

    public static Airplane buildAirplane() {
        Airplane a = new Airplane();
        a.setBrand("Boeing");
        a.setModel("787 Dreamliner");
        a.setCapacity(250);
        return a;
    }

    public static Flight buildFlight(
            String airlineCode, String airlineName,
            String originIata, String originName, String originCity, ZoneId originTimeZone,
            String destIata, String destName, String destCity, ZoneId destinationTimeZone,
            LocalDateTime departure, LocalDateTime arrival
    ) {
        Flight f = new Flight();
        f.setAirline(buildAirline(airlineCode, airlineName));
        f.setOrigin(buildAirport(originIata, originName, originCity, originTimeZone));
        f.setDestination(buildAirport(destIata, destName, destCity, destinationTimeZone));
        f.setPlane(buildAirplane());
        f.setEstimatedLocalDepartureTime(departure);
        f.setEstimatedLocalArrivalTime(arrival);
        return f;
    }

    public static Flight buildValidFlight() {
        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        ZoneId newYorkZone = ZoneId.of("America/New_York");
        return buildFlight(
                "IB", "Iberia",
                "MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", madridZone,
                "JFK", "John F. Kennedy International", "New York", newYorkZone,
                departure, departure.plusHours(8)
        );
    }

    public static Flight buildFlightWithNullOrigin() {
        Flight f = buildValidFlight();
        f.setOrigin(null);
        return f;
    }

    public static Flight buildFlightWithNullDestination() {
        Flight f = buildValidFlight();
        f.setDestination(null);
        return f;
    }

    public static Flight buildFlightWithNullAirline() {
        Flight f = buildValidFlight();
        f.setAirline(null);
        return f;
    }

    public static Flight buildFlightWithNullPlane() {
        Flight f = buildValidFlight();
        f.setPlane(null);
        return f;
    }
}
