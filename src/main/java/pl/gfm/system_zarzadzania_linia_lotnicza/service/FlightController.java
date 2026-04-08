package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.UserRepository;

import java.time.LocalDateTime;

@Controller
public class FlightController {

    private final FlightService flightService;
    private final UserRepository userRepository;

    public FlightController(FlightService flightService, UserRepository userRepository) {
        this.flightService = flightService;
        this.userRepository = userRepository;
    }

    @GetMapping("/kalendarz")
    public String pokazKalendarz(Model model) {
        // Pobieramy dane przygotowane przez chłopaków
        model.addAttribute("scheduledFlights", flightService.getAllFlights());
        model.addAttribute("availablePlanes", flightService.getAvailablePlanes());

        // Pobieramy tylko aktywnych pilotów (Twoja i Kacpra logika z zeszłego tygodnia)
        model.addAttribute("activePilots", userRepository.findAll().stream()
                .filter(u -> u.isActive() && "PILOT".equals(u.getClass().getSimpleName().toUpperCase()))
                .toList());

        return "kalendarz-dyspozytora";
    }

    @PostMapping("/zaplanuj-lot")
    public String zaplanujLot(@RequestParam String route,
                              @RequestParam Long planeId,
                              @RequestParam Long pilotId,
                              @RequestParam String departureTime,
                              Model model) {
        try {
            // Przekazujemy dane do "obrony" Kacpra
            LocalDateTime time = LocalDateTime.parse(departureTime);
            flightService.scheduleFlight(route, planeId, pilotId, time);
            return "redirect:/kalendarz";
        } catch (Exception e) {
            // Jeśli Kacper wyrzuci błąd (np. zepsuty samolot), wyświetlamy go tutaj
            model.addAttribute("error", e.getMessage());
            return pokazKalendarz(model);
        }
    }
}