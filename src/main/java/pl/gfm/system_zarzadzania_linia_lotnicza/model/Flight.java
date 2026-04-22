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

    // NOWE POLA (Zadania 16.04 i 23.04)
    private double distance;      // Dystans trasy
    private double cargoWeight;   // Waga bagażu
    private boolean fuelApproved = false; // Czy mechanik zatankował
    private boolean notified = false;     // Czy załoga widziała powiadomienie

    @ManyToOne
    private Airplane airplane;

    @ManyToOne
    private User pilot;
}