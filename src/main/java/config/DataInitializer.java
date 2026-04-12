package config;

import model.Airline;
import model.Airplane;
import model.Airport;
import model.Flight;
import repository.AirlineRepository;
import repository.AirplaneRepository;
import repository.AirportRepository;
import repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DataInitializer implements CommandLineRunner {
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final AirplaneRepository airplaneRepository;

    public DataInitializer(FlightRepository flightRepository, AirlineRepository airlineRepository,
                           AirportRepository airportRepository, AirplaneRepository airplaneRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.airplaneRepository = airplaneRepository;
    }
    @Override
    public void run(String... args) {
        // 1. Crear Aerolínea
        Airline iberia = new Airline();
        iberia.setCode("IB");
        iberia.setName("Iberia");
        airlineRepository.save(iberia);

        // 2. Crear Aeropuertos
        Airport mad = new Airport();
        mad.setIataCode("MAD");
        mad.setAirportName("Adolfo Suárez Madrid-Barajas");
        mad.setCity("Madrid");
        mad.setTimeZone(ZoneId.of("Europe/Madrid"));
        airportRepository.save(mad);

        Airport jfk = new Airport();
        jfk.setIataCode("JFK");
        jfk.setAirportName("John F. Kennedy International");
        jfk.setCity("New York");
        jfk.setTimeZone(ZoneId.of("America/New_York"));
        airportRepository.save(jfk);

        // 3. Crear Avión
        Airplane boeing = new Airplane();
        boeing.setBrand("Boeing");
        boeing.setModel("787 Dreamliner");
        boeing.setCapacity(250);
        airplaneRepository.save(boeing);

        // 4. Crear Vuelo (Hoy)
        Flight f1 = new Flight();
        f1.setAirline(iberia);
        f1.setOrigin(mad);
        f1.setDestination(jfk);
        f1.setPlane(boeing);
        f1.setEstimatedLocalDepartureTime(LocalDateTime.now());
        f1.setEstimatedLocalArrivalTime(LocalDateTime.now().plusHours(8));
        flightRepository.save(f1);

        System.out.println(">> Datos de prueba cargados correctamente.");
    }
}
