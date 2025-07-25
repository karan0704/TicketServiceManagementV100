package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticketmanagement.ticketservicemanagementv100.entity.Engineer;

import java.util.List;
import java.util.Optional;

public interface EngineerRepository extends JpaRepository<Engineer, Long> {
    Optional<Engineer> findByUsername(String username); // New method for finding engineer by username

    Optional<Engineer> findByEmail(String email);

    Optional<Engineer> findByIsDefaultEngineer(boolean isDefault);

    List<Engineer> findByIsDefaultEngineerFalse();
}
