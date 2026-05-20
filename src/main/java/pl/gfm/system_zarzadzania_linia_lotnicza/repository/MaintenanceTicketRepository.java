package pl.gfm.system_zarzadzania_linia_lotnicza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.MaintenanceTicket;
import java.util.List;

public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Long> {
    List<MaintenanceTicket> findByAirplaneId(Long airplaneId);

    List<MaintenanceTicket> findByStatus(String status);
}