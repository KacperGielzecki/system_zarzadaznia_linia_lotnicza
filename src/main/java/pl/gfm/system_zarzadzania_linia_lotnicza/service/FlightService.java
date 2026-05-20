package pl.gfm.system_zarzadzania_linia_lotnicza.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.*;
import pl.gfm.system_zarzadzania_linia_lotnicza.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirplaneRepository airplaneRepository;
    private final UserRepository userRepository;
    private final MaintenanceTicketRepository maintenanceTicketRepository; // Dodane repozytorium usterek

    public FlightService(FlightRepository flightRepository,
                         AirplaneRepository airplaneRepository,
                         UserRepository userRepository,
                         MaintenanceTicketRepository maintenanceTicketRepository) {
        this.flightRepository = flightRepository;
        this.airplaneRepository = airplaneRepository;
        this.userRepository = userRepository;
        this.maintenanceTicketRepository = maintenanceTicketRepository;
    }

    @Transactional
    public void scheduleFlight(String route, Long planeId, Long pilotId, LocalDateTime departureTime,
                               double distance, double cargoWeight, double passengerWeight) {

        Airplane plane = airplaneRepository.findById(planeId).orElseThrow(() -> new IllegalArgumentException("Błąd samolotu"));
        User user = userRepository.findById(pilotId).orElseThrow(() -> new IllegalArgumentException("Błąd pilota"));

        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Błąd: Nie można zaplanować lotu z datą wsteczną!");
        }

        if (!plane.isFunctional()) throw new IllegalArgumentException("Samolot jest niesprawny!");

        boolean hasCriticalDefect = plane.getTickets().stream()
                .anyMatch(t -> t.isCzyKrytyczna() && "OPEN".equals(t.getStatus()));

        if (hasCriticalDefect) {
            throw new IllegalArgumentException("Odmowa: Samolot posiada aktywne usterki krytyczne!");
        }

        if (!(user instanceof Pilot pilot)) {
            throw new IllegalArgumentException("Wybrany użytkownik nie jest pilotem!");
        }

        if (!pilot.isActive()) throw new IllegalArgumentException("Pilot jest nieaktywny!");

        LocalDate flightDate = departureTime.toLocalDate();
        if (pilot.getMedicalExamExpiryDate() == null || pilot.getMedicalExamExpiryDate().isBefore(flightDate)) {
            throw new IllegalArgumentException("Odmowa: Badania lekarskie pilota są nieważne w dniu lotu!");
        }

        if (pilot.getLicenseExpiryDate() == null || pilot.getLicenseExpiryDate().isBefore(flightDate)) {
            throw new IllegalArgumentException("Odmowa: Licencja pilota jest nieważna w dniu lotu!");
        }

        String requiredModel = plane.getModel();
        if (pilot.getAllowedModels() == null || !pilot.getAllowedModels().contains(requiredModel)) {
            throw new IllegalArgumentException("Pilot nie ma uprawnień na model: " + requiredModel);
        }

        if (pilot.getLastFlightEndTime() != null && pilot.getLastFlightEndTime().plusHours(12).isAfter(departureTime)) {
            throw new IllegalArgumentException("Odmowa: Pilot musi odpocząć minimum 12h od zakończenia poprzedniego lotu!");
        }

        double totalWeight = cargoWeight + passengerWeight;
        if (totalWeight > 15000) throw new IllegalArgumentException("Przeciążenie! Max dopuszczalna masa to 15000kg.");

        double calculatedFuel = (distance * 4) + 500 + (totalWeight * 0.05);

        Flight flight = new Flight();
        flight.setRoute(route);
        flight.setAirplane(plane);
        flight.setPilot(pilot);
        flight.setDepartureTime(departureTime);
        flight.setDistance(distance);
        flight.setCargoWeight(cargoWeight);
        flight.setPassengerWeight(passengerWeight);
        flight.setRequiredFuel(calculatedFuel);
        flight.setFuelApproved(false);
        flight.setLoadsheetAccepted(false);

        pilot.setLastFlightEndTime(departureTime.plusHours(2));
        userRepository.save(pilot);
        flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Airplane> getAvailablePlanes() {
        return airplaneRepository.findByFunctionalTrue();
    }
}