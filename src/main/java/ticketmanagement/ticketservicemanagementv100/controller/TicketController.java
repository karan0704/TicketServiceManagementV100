package ticketmanagement.ticketservicemanagementv100.controller;

import ticketmanagement.ticketservicemanagementv100.dto.TicketCreationDTO;
import ticketmanagement.ticketservicemanagementv100.model.Customer;
import ticketmanagement.ticketservicemanagementv100.model.Ticket;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;
import ticketmanagement.ticketservicemanagementv100.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Ticket resources.
 * This controller handles HTTP requests related to tickets,
 * delegating business logic to the TicketService.
 */
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService; // Inject the service
    private final CustomerRepository customerRepo;

    /**
     * Creates a new Ticket using a TicketCreationDTO.
     * This endpoint expects a DTO containing the ticket description,
     * the customerId, and an optional engineerId for immediate assignment.
     *
     * @param ticketDto The TicketCreationDTO object containing description, customerId, and optional engineerId.
     * @return ResponseEntity containing the created Ticket and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketCreationDTO ticketDto,
                                               @RequestHeader("X-Username") String username,
                                               @RequestHeader("X-User-Role") String role) {
        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (ticketDto.getDescription() == null || ticketDto.getDescription().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Customer customer = customerRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Ticket savedTicket = ticketService.createTicket(
                customer.getId(),
                ticketDto.getDescription(),
                ticketDto.getEngineerId()
        );

        return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
    }


    /**
     * Retrieves all Tickets.
     *
     * @return ResponseEntity containing a list of all Tickets and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Retrieves a Ticket by its ID.
     *
     * @param id The ID of the ticket to retrieve.
     * @return ResponseEntity containing the Ticket if found (HTTP 200 OK),
     * or HTTP status 404 (Not Found) if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing Ticket.
     *
     * @param id    The ID of the ticket to update.
     * @param input The Ticket object with updated details.
     * @return ResponseEntity containing the updated Ticket (HTTP 200 OK),
     * or HTTP status 404 (Not Found) if the ticket does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket input) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(id, input);
            return ResponseEntity.ok(updatedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a Ticket by its ID.
     *
     * @param id The ID of the ticket to delete.
     * @return ResponseEntity with HTTP status 200 (OK) if deleted successfully,
     * or HTTP status 404 (Not Found) if the ticket does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Acknowledges a ticket by assigning an engineer.
     *
     * @param ticketId   The ID of the ticket to acknowledge.
     * @param engineerId The ID of the engineer acknowledging the ticket.
     * @return ResponseEntity containing the acknowledged Ticket (HTTP 200 OK),
     * or HTTP status 404 (Not Found) if ticket or engineer does not exist.
     */
    @PutMapping("/{ticketId}/acknowledge/{engineerId}")
    public ResponseEntity<Ticket> acknowledgeTicket(
            @PathVariable Long ticketId,
            @PathVariable Long engineerId) {
        try {
            Ticket acknowledgedTicket = ticketService.acknowledgeTicket(ticketId, engineerId);
            return ResponseEntity.ok(acknowledgedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
