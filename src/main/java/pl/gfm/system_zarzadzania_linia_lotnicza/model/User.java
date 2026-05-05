package pl.gfm.system_zarzadzania_linia_lotnicza.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String pesel;

    private boolean active = true;

    // NOWE POLE (Zadanie 16.04)
    private LocalDateTime lastFlightEndTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastFlightEndTime() { return lastFlightEndTime; }
    public void setLastFlightEndTime(LocalDateTime lastFlightEndTime) { this.lastFlightEndTime = lastFlightEndTime; }
}