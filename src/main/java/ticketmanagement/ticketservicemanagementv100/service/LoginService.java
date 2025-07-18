package ticketmanagement.ticketservicemanagementv100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.model.User;
import ticketmanagement.ticketservicemanagementv100.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password)); // password validation
    }
}