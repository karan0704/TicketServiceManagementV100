package ticketmanagement.ticketservicemanagementv100.controller;

import ticketmanagement.ticketservicemanagementv100.dto.CustomerRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.model.Customer;
import ticketmanagement.ticketservicemanagementv100.model.UserRole;
import ticketmanagement.ticketservicemanagementv100.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRegistrationDTO registrationDTO) {
        if (registrationDTO.getUsername() == null || registrationDTO.getPassword() == null || registrationDTO.getUsername().trim().isEmpty() || registrationDTO.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Basic validation
        }
        Customer customer = new Customer();
        customer.setUsername(registrationDTO.getUsername());
        customer.setPassword(registrationDTO.getPassword());
        customer.setRole(UserRole.CUSTOMER);
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById((id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
