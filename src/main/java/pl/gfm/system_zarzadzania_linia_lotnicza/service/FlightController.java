package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FlightController {

    private final FlightService flightService;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    public FlightController(FlightService flightService, UserRepository userRepository, FlightRepository flightRepository) {
        this.flightService = flightService;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @GetMapping("/kalendarz")
    public String pokazKalendarz(Model model) {
        model.addAttribute("scheduledFlights", flightService.getAllFlights());
        model.addAttribute("availablePlanes", flightService.getAvailablePlanes());

        // POPRAWKA: Filtrowanie po typie klasy (Zadanie 16.04)
        // Sprawdzamy czy obiekt User fizycznie jest Pilotem
        List<User> activePilots = userRepository.findAll().stream()
                .filter(u -> u.isActive() && u instanceof Pilot)
                .toList();
        model.addAttribute("activePilots", activePilots);

        return "kalendarz-dyspozytora";
    }

    @PostMapping("/zaplanuj-lot")
    public String zaplanujLot(@RequestParam String route, @RequestParam Long planeId,
                              @RequestParam Long pilotId, @RequestParam String departureTime,
                              @RequestParam double distance, @RequestParam double weight,
                              @RequestParam(defaultValue = "0") double passengerWeight, Model model) {
        try {
            // Rejestracja lotu (Zadania 09.04 - 23.04)
            flightService.scheduleFlight(route, planeId, pilotId, LocalDateTime.parse(departureTime), distance, weight);

            // POPRAWKA: Pobieramy ostatni lot, by zapisać wagę pasażerów (Zadanie 30.04)
            List<Flight> allFlights = flightRepository.findAll();
            if (!allFlights.isEmpty()) {
                Flight lastFlight = allFlights.get(allFlights.size() - 1);
                lastFlight.setPassengerWeight(passengerWeight);
                flightRepository.save(lastFlight);
            }

            return "redirect:/kalendarz";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return pokazKalendarz(model);
        }
    }

    @GetMapping("/mechanik")
    public String panelMechanika(Model model) {
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "panel-mechanika";
    }

    @PostMapping("/zatwierdz-paliwo")
    public String zatwierdzPaliwo(@RequestParam Long id, @RequestParam double sensorFuel, Model model) {
        Flight f = flightRepository.findById(id).orElseThrow();

        // Blokada czujników - tolerancja 5% (Zadanie 30.04)
        double diff = Math.abs(f.getRequiredFuel() - sensorFuel);
        if (diff > (f.getRequiredFuel() * 0.05)) {
            model.addAttribute("error", "Błąd czujników! Różnica paliwa zbyt duża. Start zablokowany.");
            return panelMechanika(model);
        }

        f.setFuelFromSensors(sensorFuel);
        f.setFuelApproved(true);
        flightRepository.save(f);
        return "redirect:/mechanik";
    }

    @GetMapping("/zaloga")
    public String panelZalogi(Model model) {
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "panel-zalogi";
    }

    @PostMapping("/zatwierdz-wywazenie")
    public String zatwierdzWywazenie(@RequestParam Long id) {
        Flight f = flightRepository.findById(id).orElseThrow();

        // Akceptacja arkusza wyważenia (Zadanie 30.04)
        if (f.isFuelApproved()) {
            f.setLoadsheetAccepted(true);
            f.setNotified(true);
            flightRepository.save(f);
        }
        return "redirect:/zaloga";
    }
}