package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class MaintenanceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String opis;
    private LocalDateTime dataZgloszenia;
    private boolean czyKrytyczna;
    private String status; // "OPEN" lub "FIXED"

    @ManyToOne
    @JoinColumn(name = "airplane_id")
    private Airplane airplane;
}