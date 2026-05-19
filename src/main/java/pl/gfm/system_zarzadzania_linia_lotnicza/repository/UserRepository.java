package pl.gfm.system_zarzadzania_linia_lotnicza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.gfm.system_zarzadzania_linia_lotnicza.model.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPesel(String pesel);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}