package pl.gfm.system_zarzadzania_linia_lotnicza.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.service.FlightService;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.*;
import java.time.LocalDateTime;

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
        model.addAttribute("activePilots", userRepository.findAll().stream().filter(User::isActive).toList());
        return "kalendarz-dyspozytora";
    }

    @PostMapping("/zaplanuj-lot")
    public String zaplanujLot(@RequestParam String route, @RequestParam Long planeId,
                              @RequestParam Long pilotId, @RequestParam String departureTime,
                              @RequestParam double distance, @RequestParam double weight, Model model) {
        try {
            flightService.scheduleFlight(route, planeId, pilotId, LocalDateTime.parse(departureTime), distance, weight);
            return "redirect:/kalendarz";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return pokazKalendarz(model);
        }
    }

    // Zadanie 23.04
    @GetMapping("/mechanik")
    public String panelMechanika(Model model) {
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "panel-mechanika";
    }

    @PostMapping("/zatwierdz-paliwo")
    public String zatwierdzPaliwo(@RequestParam Long id) {
        Flight f = flightRepository.findById(id).orElseThrow();
        f.setFuelApproved(true);
        flightRepository.save(f);
        return "redirect:/mechanik";
    }

    // Zadanie 16.04
    @GetMapping("/zaloga")
    public String panelZalogi(Model model) {
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "panel-zalogi";
    }
}