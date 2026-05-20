package pl.gfm.system_zarzadzania_linia_lotnicza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.CabinCheck;

public interface CabinCheckRepository extends JpaRepository<CabinCheck, Long> {
}