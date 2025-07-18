package ticketmanagement.ticketservicemanagementv100.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.model.User;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EngineerRepository engineerRepository;

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(customerRepository.findAll());
        allUsers.addAll(engineerRepository.findAll());
        return allUsers;
    }

    public Optional<User> getUserById(Long id) {
        // Try to find in customers first
        Optional<User> customer = customerRepository.findById(id).map(c -> (User) c);
        if (customer.isPresent()) {
            return customer;
        }

        // If not found in customers, try engineers
        return engineerRepository.findById(id).map(e -> (User) e);
    }

    // Note: createUser method removed as you should use CustomerService or EngineerService
    // to create specific user types with proper roles

    // Note: updateUser method removed as you should use CustomerService or EngineerService
    // to update specific user types

    // Note: deleteUser method removed as you should use CustomerService or EngineerService
    // to delete specific user types

    public Optional<User> findByUsername(String username) {
        // First try to find in customers
        Optional<User> customer = customerRepository.findByUsername(username)
                .map(c -> (User) c);

        if (customer.isPresent()) {
            return customer;
        }

        // If not found in customers, try engineers
        return engineerRepository.findByUsername(username)
                .map(e -> (User) e);
    }
}