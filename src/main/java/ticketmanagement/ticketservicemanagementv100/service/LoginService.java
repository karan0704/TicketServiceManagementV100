package ticketmanagement.ticketservicemanagementv100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.model.Customer;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.model.User;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final CustomerRepository customerRepository;
    private final EngineerRepository engineerRepository;

    public User authenticate(String username, String password) {
        // Try to find as Customer
        Customer customer = customerRepository.findByUsername(username).orElse(null);
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }

        // Try to find as Engineer
        Engineer engineer = engineerRepository.findByUsername(username).orElse(null);
        if (engineer != null && engineer.getPassword().equals(password)) {
            return engineer;
        }

        return null; // Authentication failed
    }
}
