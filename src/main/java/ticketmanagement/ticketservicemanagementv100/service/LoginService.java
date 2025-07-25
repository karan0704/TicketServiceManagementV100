package ticketmanagement.ticketservicemanagementv100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final CustomerRepository customerRepository;
    private final EngineerRepository engineerRepository;

    public Optional<User> authenticate(String username, String password) {
        // First try to find in customers
        Optional<User> customer = customerRepository.findByUsername(username)
                .map(c -> (User) c)
                .filter(user -> user.getPassword().equals(password));

        if (customer.isPresent()) {
            return customer;
        }

        // If not found in customers, try engineers
        return engineerRepository.findByUsername(username)
                .map(e -> (User) e)
                .filter(user -> user.getPassword().equals(password));
    }
}