package ticketmanagement.ticketservicemanagementv100.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.dto.CustomerRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.model.Customer;
import ticketmanagement.ticketservicemanagementv100.model.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Creates a new Customer.
     *
     * @param customer The Customer object to be saved.
     * @return The saved Customer object.
     */
    public Customer createCustomer(Customer customer) {
        customer.setRole(UserRole.CUSTOMER);
        return customerRepository.save(customer);
    }

    /**
     * Retrieves all Customers.
     *
     * @return A list of all Customer objects.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Retrieves a Customer by their ID.
     *
     * @param id The ID of the customer to retrieve.
     * @return An Optional containing the Customer if found, or empty if not.
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Updates an existing Customer.
     *
     * @param id The ID of the customer to update.
     * @param customerDetails The Customer object containing updated details.
     * @return The updated Customer object.
     * @throws EntityNotFoundException if no customer with the given ID is found.
     */
    public Customer updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setUsername(customerDetails.getUsername());
                    customer.setPassword(customerDetails.getPassword());
                    return customerRepository.save(customer);
                })
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id " + id));
    }

    /**
     * Deletes a Customer by their ID.
     *
     * @param id The ID of the customer to delete.
     * @throws EntityNotFoundException if no customer with the given ID is found.
     */
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id " + id);
        }
        customerRepository.deleteById(id);
    }

    public Customer registerCustomerFromDTO(CustomerRegistrationDTO dto) {
        if (dto.getUsername() == null || dto.getPassword() == null ||
                dto.getUsername().isBlank() || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username and password must not be blank.");
        }

        Customer customer = new Customer();
        customer.setUsername(dto.getUsername());
        customer.setPassword(dto.getPassword()); // ❗ plain text for now
        customer.setRole(UserRole.CUSTOMER);

        return customerRepository.save(customer);
    }

}