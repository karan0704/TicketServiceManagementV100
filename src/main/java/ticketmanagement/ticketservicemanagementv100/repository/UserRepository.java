package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom method to find a User by username.
    // Spring Data JPA will automatically implement this based on the method name.
    Optional<User> findByUsername(String username);
}
