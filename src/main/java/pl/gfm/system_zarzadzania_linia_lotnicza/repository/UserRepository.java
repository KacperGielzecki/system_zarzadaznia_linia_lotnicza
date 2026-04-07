package pl.gfm.system_zarzadzania_linia_lotnicza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPesel(String pesel); // Metoda defensywna do sprawdzania duplikatów
}