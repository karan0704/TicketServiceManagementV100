package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;

import java.util.Optional;

@Repository
public interface EngineerRepository extends JpaRepository<Engineer, Long> {
    Optional<Engineer> findByUsername(String username); // New method for finding engineer by username
}
