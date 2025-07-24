package ticketmanagement.ticketservicemanagementv100.controller;

import ticketmanagement.ticketservicemanagementv100.dto.TicketCreationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.TicketUpdateDTO;
import ticketmanagement.ticketservicemanagementv100.model.Customer;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.model.Ticket;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.TicketRepository;
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
 *
 * Updated: Changed base request mapping to "/api/tickets" to match frontend.
 */
@RestController
@RequestMapping("/api/tickets") // Changed to /api/tickets
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService; // Inject the service
    private final CustomerRepository customerRepo;
    private final EngineerRepository  engineerRepo;
    private final TicketRepository ticketRepo;

    /**
     * Creates a new Ticket using a TicketCreationDTO.
     * This endpoint expects a DTO containing the ticket description,
     * the customerId, and an optional engineerId for immediate assignment.
     *
     * @param ticketDto The TicketCreationDTO object containing description, customerId, and optional engineerId.
     * @param username The username of the customer creating the ticket (from X-Username header).
     * @param role The role of the user (from X-User-Role header).
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
     * @param dto The Ticket object with updated details.
     * @return ResponseEntity containing the updated Ticket (HTTP 200 OK),
     * or HTTP status 404 (Not Found) if the ticket does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id,
                                               @RequestBody TicketUpdateDTO dto) {
        try {
            Ticket ticket = ticketService.getTicketById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

            if (dto.getEngineerId() != null) {
                Engineer engineer = engineerRepo.findById(dto.getEngineerId())
                        .orElseThrow(() -> new EntityNotFoundException("Engineer not found"));
                ticket.setAcknowledgedBy(engineer);
            }

            ticket.setDescription(dto.getDescription());
            ticket.setStatus(dto.getStatus());
            ticket.setTentativeResolutionDate(dto.getTentativeResolutionDate());
            ticket.setCustomerCommentOnTicket(dto.getCustomerCommentOnTicket());
            ticket.setEngineerCommentOnTicket(dto.getEngineerCommentOnTicket());

            Ticket updated = ticketRepo.save(ticket);
            return ResponseEntity.ok(updated);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Deletes a Ticket by its ID.
     *
     * @param id The ID of the ticket to delete.
     * @param role The role of the user (from X-User-Role header).
     * @return ResponseEntity with HTTP status 200 (OK) if deleted successfully,
     * or HTTP status 404 (Not Found) if the ticket does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id,
                                             @RequestHeader("X-User-Role") String role) {
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ticketService.deleteTicket(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Acknowledges a ticket by assigning the currently logged-in engineer.
     * The engineer's username is retrieved from the X-Username header.
     *
     * @param ticketId The ID of the ticket to acknowledge.
     * @param engineerUsername The username of the engineer acknowledging the ticket (from X-Username header).
     * @return ResponseEntity containing the acknowledged Ticket (HTTP 200 OK),
     * or HTTP status 404 (Not Found) if ticket or engineer does not exist.
     * @return ResponseEntity with HTTP status 403 (Forbidden) if the user is not an ENGINEER.
     */
    @PutMapping("/{ticketId}/acknowledge") // Modified endpoint: removed {engineerId} from path
    public ResponseEntity<Ticket> acknowledgeTicket(
            @PathVariable Long ticketId,
            @RequestHeader("X-Username") String engineerUsername, // Get username from header
            @RequestHeader("X-User-Role") String role) { // Get role from header for validation
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // Call service method with username
            Ticket acknowledgedTicket = ticketService.acknowledgeTicket(ticketId, engineerUsername);
            return ResponseEntity.ok(acknowledgedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get tickets created by a specific customer (by username)
     * Called by frontend: GET /api/tickets/customer/{username}
     */
    @GetMapping("/customer/{username}")
    public ResponseEntity<List<Ticket>> getTicketsByCustomer(
            @PathVariable String username,
            @RequestHeader("X-Username") String requestUsername,
            @RequestHeader("X-User-Role") String role) {

        // Security check: ensure customer can only access their own tickets
        if (!"CUSTOMER".equalsIgnoreCase(role) || !username.equals(requestUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Customer customer = customerRepo.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

            List<Ticket> tickets = ticketRepo.findByCreatedById(customer.getId());
            return ResponseEntity.ok(tickets);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get tickets assigned to a specific engineer (by username)
     * Called by frontend: GET /api/tickets/engineer/{username}
     */
    @GetMapping("/engineer/{username}")
    public ResponseEntity<List<Ticket>> getTicketsByEngineer(
            @PathVariable String username,
            @RequestHeader("X-Username") String requestUsername,
            @RequestHeader("X-User-Role") String role) {

        // Security check: ensure engineer can only access their own assigned tickets
        if (!"ENGINEER".equalsIgnoreCase(role) || !username.equals(requestUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Engineer engineer = engineerRepo.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Engineer not found"));

            List<Ticket> tickets = ticketRepo.findByAcknowledgedById(engineer.getId());
            return ResponseEntity.ok(tickets);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}