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

    public FlightService(FlightRepository flightRepository,
                         AirplaneRepository airplaneRepository,
                         UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.airplaneRepository = airplaneRepository;
        this.userRepository = userRepository;
    }

    /**
     * GŁÓWNA LOGIKA ZABEZPIECZEŃ (Zadanie Kacpra)
     */
    public void scheduleFlight(String route, Long planeId, Long pilotId, LocalDateTime departureTime) {

        // 1. Pobieranie danych z bazy (Zadanie Mateusza)
        Airplane plane = airplaneRepository.findById(planeId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wybranego samolotu!"));

        User pilot = userRepository.findById(pilotId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wybranego pilota!"));

        // 2. OBRONA: Czy samolot nie jest zepsuty?
        if (!plane.isFunctional()) {
            throw new IllegalArgumentException("BŁĄD KRYTYCZNY: Samolot " + plane.getRegistrationNumber() + " jest zgłoszony jako NIESPRAWNY!");
        }

        // 3. OBRONA: Czy pilot ma ważne uprawnienia?
        // Wykorzystujemy pole 'active', które Klaudia i Kacper ustawili w poprzednim kroku
        if (!pilot.isActive()) {
            throw new IllegalArgumentException("BŁĄD: Pilot " + pilot.getLastName() + " ma nieważne badania lub licencję!");
        }

        // 4. OBRONA: Czy data lotu nie jest z przeszłości?
        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("BŁĄD: Nie można zaplanować lotu w przeszłości!");
        }

        // Jeśli wszystko OK -> Tworzymy i zapisujemy lot
        Flight flight = new Flight();
        flight.setRoute(route);
        flight.setAirplane(plane);
        flight.setPilot(pilot);
        flight.setDepartureTime(departureTime);

        flightRepository.save(flight);
    }

    // Metody pomocnicze dla kontrolera Klaudii
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Airplane> getAvailablePlanes() {
        return airplaneRepository.findByFunctionalTrue();
    }
}