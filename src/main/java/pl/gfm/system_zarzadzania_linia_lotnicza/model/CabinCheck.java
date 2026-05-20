package pl.gfm.system_zarzadzania_linia_lotnicza.model;
import jakarta.persistence.*;

@Entity
public class CabinCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean passengersVerified;
    private boolean equipmentVerified;

    @OneToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    // Pamiętaj, żeby dodać też konstruktory, gettery i settery!
    public CabinCheck() {}

    public Long getId() { return id; }
    public boolean isPassengersVerified() { return passengersVerified; }
    public void setPassengersVerified(boolean passengersVerified) { this.passengersVerified = passengersVerified; }
    public boolean isEquipmentVerified() { return equipmentVerified; }
    public void setEquipmentVerified(boolean equipmentVerified) { this.equipmentVerified = equipmentVerified; }
    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
}