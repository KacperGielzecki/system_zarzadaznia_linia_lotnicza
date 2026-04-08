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

    @ManyToOne
    private Airplane airplane;

    @ManyToOne
    private User pilot; // Pilot to typ Usera
}