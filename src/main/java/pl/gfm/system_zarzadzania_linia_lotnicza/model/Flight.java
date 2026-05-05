package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String route;
    private LocalDateTime departureTime;

    // ETAP 16.04
    private double distance;              // Dystans trasy w km
    private boolean notified = false;     // Czy załoga widziała powiadomienie o locie

    // ETAP 23.04
    private double cargoWeight;           // Waga bagażu/cargo
    private double passengerWeight;       // Waga pasażerów (potrzebna do wyważenia 30.04)
    private double requiredFuel;          // Wyliczone zapotrzebowanie na paliwo
    private boolean fuelApproved = false; // Czy mechanik zatwierdził tankowanie

    // ETAP 30.04
    private double fuelFromSensors;       // Ilość paliwa odczytana z czujników samolotu
    private boolean loadsheetAccepted = false; // Czy pilot zatwierdził arkusz wyważenia

    @ManyToOne
    private Airplane airplane;

    @ManyToOne
    private User pilot;

    // Pomocnicza metoda do obliczenia całkowitej masy startowej
    public double getTotalWeight() {
        return cargoWeight + passengerWeight + requiredFuel;
    }
}