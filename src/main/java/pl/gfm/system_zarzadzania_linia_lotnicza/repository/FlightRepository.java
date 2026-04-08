package pl.gfm.system_zarzadzania_linia_lotnicza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Tutaj na razie wystarczą podstawowe metody zapisu i odczytu z JpaRepository
}