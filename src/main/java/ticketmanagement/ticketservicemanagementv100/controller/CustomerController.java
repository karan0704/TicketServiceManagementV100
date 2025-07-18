package ticketmanagement.ticketservicemanagementv100.controller;

import org.springframework.http.HttpStatus;
import ticketmanagement.ticketservicemanagementv100.dto.CustomerRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.CustomerResponseDTO;
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
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRegistrationDTO registrationDTO) {
        Customer savedCustomer = customerService.registerCustomerFromDTO(registrationDTO);
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(savedCustomer.getId(), savedCustomer.getUsername());
        return ResponseEntity.ok(customerResponseDTO);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id,
                                               @RequestHeader("X-User-Role") String role) { // Add role for restriction
        // Role restriction: Only ENGINEER role can delete
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        customerService.deleteCustomer(id); // This calls the method you already have in CustomerService
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
