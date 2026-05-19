package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    private String registrationNumber;
    private boolean functional = true;

    @OneToMany(mappedBy = "airplane", cascade = CascadeType.ALL)
    private List<MaintenanceTicket> tickets = new ArrayList<>();
}