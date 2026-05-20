package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.*;

@Controller
public class StewardessController {

    private final FlightRepository flightRepository;
    private final CabinCheckRepository cabinCheckRepository;

    public StewardessController(FlightRepository flightRepository, CabinCheckRepository cabinCheckRepository) {
        this.flightRepository = flightRepository;
        this.cabinCheckRepository = cabinCheckRepository;
    }

    @GetMapping("/stewardessa")
    public String panelStewardessy(HttpSession session, Model model) {
        // Tylko loty z paliwem (fuelApproved=true) i bez jeszcze wykonanej kontroli kabiny
        model.addAttribute("flightsToVerify", flightRepository.findAll().stream()
                .filter(f -> f.isFuelApproved() && f.getCabinCheck() == null)
                .toList());
        return "panel-stewadressa";
    }

    @PostMapping("/stewardessa/zatwierdz")
    public String zatwierdzKabinę(@RequestParam Long flightId,
                                  @RequestParam boolean passengersVerified,
                                  @RequestParam boolean equipmentVerified) {
        Flight f = flightRepository.findById(flightId).orElseThrow();

        CabinCheck check = new CabinCheck();
        check.setFlight(f);
        check.setPassengersVerified(passengersVerified);
        check.setEquipmentVerified(equipmentVerified);

        cabinCheckRepository.save(check);
        return "redirect:/stewardessa";
    }
}