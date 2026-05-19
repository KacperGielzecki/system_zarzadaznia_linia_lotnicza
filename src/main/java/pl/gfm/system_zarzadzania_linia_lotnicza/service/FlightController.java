package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import jakarta.servlet.http.HttpSession;
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
    private final AirplaneRepository airplaneRepository;

    public FlightController(FlightService flightService,
                            UserRepository userRepository,
                            FlightRepository flightRepository,
                            AirplaneRepository airplaneRepository) {
        this.flightService = flightService;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.airplaneRepository = airplaneRepository;
    }

    // --- KALENDARZ DYSPOZYTORA ---
    @GetMapping("/kalendarz")
    public String pokazKalendarz(HttpSession session, Model model) {
        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Dispatcher)) {
            return "redirect:/login?error=BrakDostepu";
        }

        model.addAttribute("scheduledFlights", flightService.getAllFlights());
        model.addAttribute("availablePlanes", flightService.getAvailablePlanes());
        model.addAttribute("activePilots", userRepository.findAll().stream()
                .filter(u -> u.isActive() && u instanceof Pilot)
                .toList());

        return "kalendarz-dyspozytora";
    }

    // --- ZAPLANUJ LOT (DYSPOZYTOR) - POPRAWIONA METODA Z SERWISEM ---
    @PostMapping("/zaplanuj-lot")
    public String zaplanujLot(@RequestParam String route,
                              @RequestParam double distance,
                              @RequestParam double cargoWeight,
                              @RequestParam double passengerWeight,
                              @RequestParam Long planeId,
                              @RequestParam Long pilotId,
                              @RequestParam LocalDateTime departureTime,
                              HttpSession session,
                              Model model) {

        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Dispatcher)) {
            return "redirect:/login?error=BrakDostepu";
        }

        try {
            // Wykorzystujemy serwis, który sprawdza uprawnienia, odpoczynek i liczy paliwo!
            flightService.scheduleFlight(route, planeId, pilotId, departureTime, distance, cargoWeight, passengerWeight);
            return "redirect:/kalendarz?success=LotZaplanowany";
        } catch (IllegalArgumentException e) {
            // W razie błędu (np. pilot bez uprawnień) wracamy do kalendarza z błędem
            model.addAttribute("error", e.getMessage());
            model.addAttribute("scheduledFlights", flightService.getAllFlights());
            model.addAttribute("availablePlanes", flightService.getAvailablePlanes());
            model.addAttribute("activePilots", userRepository.findAll().stream()
                    .filter(u -> u.isActive() && u instanceof Pilot).toList());
            return "kalendarz-dyspozytora";
        }
    }

    // --- PANEL MECHANIKA ---
    @GetMapping("/mechanik")
    public String panelMechanika(HttpSession session, Model model) {
        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Mechanic)) {
            return "redirect:/login?error=BrakDostepu";
        }
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "panel-mechanika";
    }

    @PostMapping("/zatwierdz-paliwo")
    public String zatwierdzPaliwo(@RequestParam Long id, @RequestParam double sensorFuel, HttpSession session, Model model) {
        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Mechanic)) {
            return "redirect:/login?error=BrakDostepu";
        }

        Flight f = flightRepository.findById(id).orElseThrow();
        double diff = Math.abs(f.getRequiredFuel() - sensorFuel);

        if (diff > (f.getRequiredFuel() * 0.05)) {
            model.addAttribute("error", "Błąd czujników! Różnica paliwa zbyt duża.");
            model.addAttribute("allFlights", flightService.getAllFlights());
            return "panel-mechanika";
        }

        f.setFuelFromSensors(sensorFuel);
        f.setFuelApproved(true);
        flightRepository.save(f);
        return "redirect:/mechanik";
    }

    // --- PANEL ZAŁOGI (PILOT) ---
    @GetMapping("/zaloga")
    public String panelZalogi(HttpSession session, Model model) {
        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Pilot)) {
            return "redirect:/login?error=BrakDostepu";
        }
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "panel-zalogi";
    }

    @PostMapping("/zatwierdz-wywazenie")
    public String zatwierdzWywazenie(@RequestParam Long id, HttpSession session) {
        User uzytkownik = (User) session.getAttribute("zalogowanyUzytkownik");
        if (uzytkownik == null || !(uzytkownik instanceof Pilot)) {
            return "redirect:/login?error=BrakDostepu";
        }

        Flight f = flightRepository.findById(id).orElseThrow();
        f.setLoadsheetAccepted(true);
        flightRepository.save(f);
        return "redirect:/zaloga";
    }
}