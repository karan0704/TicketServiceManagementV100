package ticketmanagement.ticketservicemanagementv100.repository;

import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
    List<User> findByRole(UserRole role);
    Optional<User> findByIsDefaultEngineerTrue();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
