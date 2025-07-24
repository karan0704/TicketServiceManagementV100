package ticketmanagement.ticketservicemanagementv100.service;

import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    
    public User authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElse(null);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public User findDefaultEngineer() {
        return userRepository.findByIsDefaultEngineerTrue().orElse(null);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean isDefaultEngineer(User user) {
        return user != null && user.getIsDefaultEngineer();
    }
}
