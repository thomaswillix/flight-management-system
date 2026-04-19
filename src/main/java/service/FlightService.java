package service;

import model.*;
import notification.FlightStateListener;
import repository.AirlineRepository;
import org.springframework.stereotype.Service;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static model.Airline.requireValidAirlineCode;
import static utils.Validation.requireNotBlank;
import static utils.Validation.requireNotNull;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final List<FlightStateListener> flightStateListeners;
    private final AirplaneRepository airplaneRepository;

    public FlightService(FlightRepository flightRepository,
                         List<FlightStateListener> flightStateListeners,
                         AirportRepository airportRepository,
                         AirlineRepository airlineRepository,
                         AirplaneRepository airplaneRepository
    ) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.flightStateListeners = flightStateListeners;
        this.airplaneRepository = airplaneRepository;
    }

    // Cantidad de vuelos por aerolínea
    public int getAirlineFlightsQuantity(Airline airline){
        requireNotNull(airline, "Airline");
        return flightRepository.findAllFlightsByAirline(airline.getAirlineCode()).size();
    }

    // Lista de pasajeros de un número de vuelo y aerolínea pasados por parámetro
    public List<Passenger> getPassengersList(String flightNumber, Airline airline){
        requireNotBlank(flightNumber, "Flight number");
        requireNotNull(airline, "Airline");
        Flight flight = flightRepository.findFlightByNumber(airline,flightNumber);
        if (flight == null){
            throw new IllegalArgumentException("Flight was not found");
        }
        if (flight instanceof CommercialFlight commercialFlight) {
            return commercialFlight.getPassengers().stream().toList();
        }
        throw new UnsupportedOperationException("Cargo flights do not have passengers");
    }

    // Generar una lista con los vuelos entre dos fechas (tomando en cuenta como fecha de salida la fecha local)
    public List<Flight> getFlightsListBetweenDates(LocalDate departureDate, LocalDate arrivalDate){
        requireNotNull(departureDate, "Departure date");
        requireNotNull(arrivalDate, "Arrival date");
        List<LocalDate> dates = departureDate.datesUntil(arrivalDate).toList();
        List<Flight> flightsBetweenDates = new ArrayList<>();
        for (LocalDate date : dates){
            flightsBetweenDates.addAll(flightRepository.findFlightsByDate(date));
        }
        return flightsBetweenDates;
    }

    // Devolver un Map con la aerolínea como clave y la cantidad de vuelos que contiene
    public Map<Airline, Integer> getFlightsQuantityPerAirline(){
        List<Airline> airlinesList = airlineRepository.findAllAirlines();
        Map<Airline, Integer> flightsPerAirline = new HashMap<>();
        for (Airline airline : airlinesList){
            flightsPerAirline.put(airline, getAirlineFlightsQuantity(airline));
        }
        return flightsPerAirline;
    }

    // Devolver el promedio de peso de los vuelos de un código de aerolínea pasada como parámetro
    public BigDecimal getAverageFlightWeightByAirlineCode(String airlineCode){
        List<Flight> flightsList = getFlightsListByAirlineCode(airlineCode);
        BigDecimal sum = BigDecimal.valueOf(0);
        for (Flight flight : flightsList){
            sum = sum.add(flight.getEstimatedFlightWeight());
        }
        return sum.divide(BigDecimal.valueOf(flightsList.size()), RoundingMode.HALF_UP);
    }

    //Devolver todos los vuelos comerciales de un código de aerolínea pasado por parámetro
    public List<Flight> getAllCommercialFlightsFromAirline(String airlineCode){
        List<Flight> flightsList = getFlightsListByAirlineCode(airlineCode);
        List<Flight> commercialFlights = new ArrayList<>();
        for (Flight flight : flightsList){
            if(flight.getClass().equals(CommercialFlight.class)) commercialFlights.add(flight);
        }
        return commercialFlights;
    }

    //Devolver todos los vuelos de carga de un código de aerolínea pasado por parámetro
    public List<Flight> getAllCargoFlightsFromAirline(String airlineCode){
        List<Flight> flightsList = getFlightsListByAirlineCode(airlineCode);
        List<Flight> cargoFlights = new ArrayList<>();
        for (Flight flight : flightsList){
            if(flight.getClass().equals(CargoFlight.class)) cargoFlights.add(flight);
        }
        return cargoFlights;
    }

    public void updateFlightState(String flightNumber, Airline airline, FlightState newState) {
        Flight flight = flightRepository.findFlightByNumber(airline, flightNumber);
        if (flight == null) throw new IllegalArgumentException("Flight was not found");

        flightStateListeners.forEach(flight::addListener);
        flight.setFlightState(newState);
    }

    // Private helper methods
    private List<Flight> getFlightsListByAirlineCode(String airlineCode){
        requireValidAirlineCode(airlineCode);
        Airline airline = airlineRepository.findAirlineByCode(airlineCode);
        Objects.requireNonNull(airline, "Airline does not exist.");
        return flightRepository.findAllFlightsByAirline(airline.getName());
    }

    public Flight save(Flight flight) {
        if (flight.getOrigin() == null || airportRepository.findById(
                flight.getOrigin().getIataCode()).isEmpty()
        )
            throw new IllegalArgumentException("El aeropuerto de origen no existe.");

        if (flight.getDestination() == null || airportRepository.findById(
                flight.getDestination().getIataCode()).isEmpty()
        )
            throw new IllegalArgumentException("El aeropuerto de destino no existe.");

        if (flight.getAirline() == null || airlineRepository.findById(
                flight.getAirline().getAirlineCode()).isEmpty()
        )
            throw new IllegalArgumentException("La aerolínea no existe.");

        if (flight.getAirplane() == null || airplaneRepository.findById(
                flight.getAirplane().getId()).isEmpty()
        )
            throw new IllegalArgumentException("El avión no existe.");

        return flightRepository.saveFlight(flight);
    }

    public Optional<Flight> findByAirlineAndNumber(String airlineCode, Long flightNumber) {
        return flightRepository.findByAirlineCodeAndId(airlineCode, flightNumber);
    }

    public List<Flight> findByDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank())
            throw new IllegalArgumentException("La fecha no puede estar vacía.");

        LocalDate date = parseDate(dateStr);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return flightRepository.findByEstimatedLocalDepartureTimeBetween(start, end);
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Formato inválido. Use YYYY-MM-DD.");
        }
    }
}
