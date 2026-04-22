package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Service;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirplaneRepository airplaneRepository;
    private final UserRepository userRepository;

    public FlightService(FlightRepository flightRepository, AirplaneRepository airplaneRepository, UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.airplaneRepository = airplaneRepository;
        this.userRepository = userRepository;
    }

    public void scheduleFlight(String route, Long planeId, Long pilotId, LocalDateTime departureTime, double distance, double weight) {
        Airplane plane = airplaneRepository.findById(planeId).orElseThrow(() -> new IllegalArgumentException("Błąd samolotu"));
        User pilot = userRepository.findById(pilotId).orElseThrow(() -> new IllegalArgumentException("Błąd pilota"));

        // 1. OBRONA: Czy samolot sprawny?
        if (!plane.isFunctional()) throw new IllegalArgumentException("Samolot jest niesprawny!");

        // 2. OBRONA: Czy pilot aktywny?
        if (!pilot.isActive()) throw new IllegalArgumentException("Pilot nie ma uprawnień!");

        // 3. OBRONA (16.04): 12h odpoczynku
        if (pilot.getLastFlightEndTime() != null && pilot.getLastFlightEndTime().plusHours(12).isAfter(departureTime)) {
            throw new IllegalArgumentException("Pilot musi odpocząć 12h!");
        }

        // 4. OBRONA (23.04): Przeciążenie (limit 15000kg)
        if (weight > 15000) throw new IllegalArgumentException("Przeciążenie! Max 15000kg.");

        Flight flight = new Flight();
        flight.setRoute(route);
        flight.setAirplane(plane);
        flight.setPilot(pilot);
        flight.setDepartureTime(departureTime);
        flight.setDistance(distance);
        flight.setCargoWeight(weight);

        flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() { return flightRepository.findAll(); }
    public List<Airplane> getAvailablePlanes() { return airplaneRepository.findByFunctionalTrue(); }
}