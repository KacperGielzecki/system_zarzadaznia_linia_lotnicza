package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void scheduleFlight(String route, Long planeId, Long pilotId, LocalDateTime departureTime, double distance, double weight) {
        Airplane plane = airplaneRepository.findById(planeId).orElseThrow(() -> new IllegalArgumentException("Błąd samolotu"));
        User user = userRepository.findById(pilotId).orElseThrow(() -> new IllegalArgumentException("Błąd pilota"));

        // BLOKADA DATY WSTECZNEJ
        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Błąd: Nie można zaplanować lotu z datą wsteczną!");
        }

        // Czy samolot sprawny?
        if (!plane.isFunctional()) throw new IllegalArgumentException("Samolot jest niesprawny!");

        // Czy pilot aktywny i ma uprawnienia na model?
        if (!user.isActive()) throw new IllegalArgumentException("Pilot jest nieaktywny!");

        if (user instanceof Pilot pilot) {
            String requiredModel = plane.getModel();
            if (pilot.getAllowedModels() == null || !pilot.getAllowedModels().contains(requiredModel)) {
                throw new IllegalArgumentException("Pilot nie ma uprawnień na model: " + requiredModel);
            }
        }

        // 12h odpoczynku
        if (user.getLastFlightEndTime() != null && user.getLastFlightEndTime().plusHours(12).isAfter(departureTime)) {
            throw new IllegalArgumentException("Odmowa: Pilot musi odpocząć minimum 12h od zakończenia poprzedniego lotu!");
        }

        // Przeciążenie
        if (weight > 15000) throw new IllegalArgumentException("Przeciążenie! Max dopuszczalna masa to 15000kg.");

        // Paliwo
        double calculatedFuel = (distance * 4) + 500;

        Flight flight = new Flight();
        flight.setRoute(route);
        flight.setAirplane(plane);
        flight.setPilot(user);
        flight.setDepartureTime(departureTime);
        flight.setDistance(distance);
        flight.setCargoWeight(weight);
        flight.setRequiredFuel(calculatedFuel);

        // AKTUALIZACJA STATUSU PILOTA
        user.setLastFlightEndTime(departureTime.plusHours(2));
        userRepository.save(user);

        flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Airplane> getAvailablePlanes() {
        return airplaneRepository.findByFunctionalTrue();
    }
}