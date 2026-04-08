package pl.gfm.system_zarzadzania_linia_lotnicza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.Airplane;

import java.util.List;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    // Ta metoda pozwoli Klaudii wyświetlić w kalendarzu tylko sprawne maszyny
    List<Airplane> findByFunctionalTrue();
}