package utils;

import model.*;
import repository.AirlineRepository;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Component
public class TestDataFactory {

    // INSTANCIA: para tests de integración, persiste en H2

    @Autowired private FlightRepository flightRepository;
    @Autowired private AirlineRepository airlineRepository;
    @Autowired private AirportRepository airportRepository;
    @Autowired private AirplaneRepository airplaneRepository;

    public static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");
    public static final ZoneId NEW_YORK_ZONE = ZoneId.of("America/New_York");

    private static final LocalDateTime DEFAULT_DEPARTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime DEFAULT_ARRIVAL = DEFAULT_DEPARTURE.plusHours(8);
    private static final Airport MAD = buildAirPort("MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", MADRID_ZONE);
    private static final Airport JFK = buildAirPort("JFK", "John F. Kennedy International", "New York", NEW_YORK_ZONE);

    public static Airline buildAirline() {
        return new Airline("IB", "Iberia");
    }

    public static Airplane buildAirplane() {
        return new Airplane("Boeing", "727-800", BigDecimal.valueOf(40000));
    }

    public static Airport buildAirPort(String iataCode, String airportName, String city, ZoneId timezone){
        return new Airport(iataCode, airportName, city, timezone);
    }

    public static Set<Passenger> buildPassengers() {
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(new Passenger("29934099J", "María",   "Martín",    "612345678"));
        passengers.add(new Passenger("18158342A", "Carlos",  "García",    "623456789"));
        passengers.add(new Passenger("48204888Q", "Laura",   "López",     "634567890"));
        passengers.add(new Passenger("80185884H", "Javier",  "Sánchez",   "645678901"));
        passengers.add(new Passenger("77271803Z", "Sofía",   "Fernández", "656789012"));
        passengers.add(new Passenger("31727850V", "Miguel",  "González",  "667890123"));
        passengers.add(new Passenger("22292824M", "Ana",     "Rodríguez", "678901234"));
        passengers.add(new Passenger("94416654K", "Pablo",   "Pérez",     "689012345"));
        passengers.add(new Passenger("14495172C", "Elena",   "Ramírez",   "690123456"));
        passengers.add(new Passenger("40186357K", "Andrés",  "Torres",    "611234567"));
        return passengers;
    }

    public static CargoFlight buildCargoFlight(LocalDateTime departure, LocalDateTime arrival, BigDecimal cargoWeight) {
        return new CargoFlight("FL001", buildAirline(), buildAirplane(),
                MAD, JFK,
                departure, arrival, departure, arrival, null, null,
                FlightState.ON_TIME, cargoWeight);
    }

    public static CommercialFlight buildCommercialFlight(LocalDateTime departure, LocalDateTime arrival,
                                                         Integer capacity, Set<Passenger> passengers) {
        return new CommercialFlight("FL001", buildAirline(), buildAirplane(),
                MAD, JFK,
                departure, arrival, departure, arrival, null, null,
                FlightState.ON_TIME, passengers, capacity);
    }

    public static CommercialFlight buildValidFlight() {
        return buildCommercialFlight(DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, 100, buildPassengers());
    }

    public static Flight buildFlightWithNullOrigin() {
        return new CommercialFlight("FL001", buildAirline(), buildAirplane(),
                null, JFK,
                DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, null, null,
                FlightState.ON_TIME, buildPassengers(), 100);
    }

    public static Flight buildFlightWithNullDestination() {
        return new CommercialFlight("FL001", buildAirline(), buildAirplane(),
                MAD, null,
                DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, null, null,
                FlightState.ON_TIME, buildPassengers(), 100);
    }

    public static Flight buildFlightWithNullAirline() {
        return new CommercialFlight("FL001", null, buildAirplane(),
                MAD, JFK,
                DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, null, null,
                FlightState.ON_TIME, buildPassengers(), 100);
    }

    public static Flight buildFlightWithNullAirplane() {
        return new CommercialFlight("FL001", buildAirline(), null,
                MAD, JFK,
                DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, DEFAULT_DEPARTURE, DEFAULT_ARRIVAL, null, null,
                FlightState.ON_TIME, buildPassengers(), 100);
    }
}
