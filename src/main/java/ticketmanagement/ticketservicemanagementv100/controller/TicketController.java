package ticketmanagement.ticketservicemanagementv100.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.TicketCreationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.TicketUpdateDTO;
import ticketmanagement.ticketservicemanagementv100.model.Customer;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.model.Ticket;
import ticketmanagement.ticketservicemanagementv100.repository.CustomerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;
import ticketmanagement.ticketservicemanagementv100.repository.TicketRepository;
import ticketmanagement.ticketservicemanagementv100.service.TicketService;
import ticketmanagement.ticketservicemanagementv100.model.TicketStatus;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TicketController {

    // Remove @Autowired when using @RequiredArgsConstructor with final fields
    private final TicketService ticketService;
    private final CustomerRepository customerRepo;
    private final EngineerRepository engineerRepo;
    private final TicketRepository ticketRepo;

    /**
     * Get tickets for engineer - both assigned and unassigned tickets
     * Called by frontend: GET /api/tickets/engineer/{username}
     */
    @GetMapping("/engineer/{username}")
    public ResponseEntity<List<Ticket>> getTicketsByEngineer(
            @PathVariable String username,
            @RequestHeader("X-Username") String requestUsername,
            @RequestHeader("X-User-Role") String role) {

        // Security check: ensure engineer can only access their own assigned/unassigned tickets view
        if (!"ENGINEER".equalsIgnoreCase(role) || !username.equals(requestUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Engineer engineer = engineerRepo.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Engineer not found"));

            // Fetch tickets assigned to this engineer
            List<Ticket> assignedTickets = ticketRepo.findByAcknowledgedById(engineer.getId());

            // Fetch unassigned tickets (acknowledgedBy is null)
            List<Ticket> unassignedTickets = ticketRepo.findByAcknowledgedByIdIsNull();

            // Combine lists
            List<Ticket> combined = new ArrayList<>();
            combined.addAll(assignedTickets);
            combined.addAll(unassignedTickets);

            return ResponseEntity.ok(combined);

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
     * Creates a new Ticket using a TicketCreationDTO.
     * This endpoint expects a DTO containing the ticket description,
     * the customerId, and an optional engineerId for immediate assignment.
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

        try {
            Customer customer = customerRepo.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

            Ticket savedTicket = ticketService.createTicket(
                    customer.getId(),
                    ticketDto.getDescription(),
                    ticketDto.getEngineerId()
            );

            return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all Tickets.
     */
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * Retrieves a Ticket by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing Ticket - supports comments, status, description, tentative date, reassignment
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id,
                                               @RequestBody TicketUpdateDTO dto,
                                               @RequestHeader("X-Username") String username,
                                               @RequestHeader("X-User-Role") String role) {
        try {
            Ticket ticket = ticketService.getTicketById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

            // Authorization check: customer who created or engineer assigned can update
            boolean isCustomer = "CUSTOMER".equalsIgnoreCase(role) &&
                    ticket.getCreatedBy().getUsername().equals(username);
            boolean isEngineer = "ENGINEER".equalsIgnoreCase(role) &&
                    ticket.getAcknowledgedBy() != null &&
                    ticket.getAcknowledgedBy().getUsername().equals(username);

            if (!(isCustomer || isEngineer)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Customer can only update their comments
            if (dto.getCustomerCommentOnTicket() != null && isCustomer) {
                ticket.setCustomerCommentOnTicket(dto.getCustomerCommentOnTicket());
            }

            // Engineer can update multiple fields
            if (isEngineer) {
                if (dto.getEngineerCommentOnTicket() != null) {
                    ticket.setEngineerCommentOnTicket(dto.getEngineerCommentOnTicket());
                }
                if (dto.getTentativeResolutionDate() != null) {
                    ticket.setTentativeResolutionDate(dto.getTentativeResolutionDate());
                }
                if (dto.getDescription() != null) {
                    ticket.setDescription(dto.getDescription());
                }
                if (dto.getStatus() != null) {
                    ticket.setStatus(dto.getStatus());
                }
                if (dto.getEngineerId() != null) {
                    Engineer newEngineer = engineerRepo.findById(dto.getEngineerId())
                            .orElseThrow(() -> new EntityNotFoundException("Engineer not found"));
                    ticket.setAcknowledgedBy(newEngineer);
                }
            }

            Ticket updated = ticketRepo.save(ticket);
            return ResponseEntity.ok(updated);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a Ticket by its ID (Engineer only).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id,
                                             @RequestHeader("X-User-Role") String role) {
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Engineer acknowledges ticket (assigns themselves to unassigned ticket)
     */
    @PutMapping("/{ticketId}/acknowledge")
    public ResponseEntity<Ticket> acknowledgeTicket(@PathVariable Long ticketId,
                                                    @RequestHeader("X-Username") String username,
                                                    @RequestHeader("X-User-Role") String role) {
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Ticket ticket = ticketService.getTicketById(ticketId)
                    .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

            // Check if ticket is already assigned
            if (ticket.getAcknowledgedBy() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Already assigned
            }

            Engineer engineer = engineerRepo.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Engineer not found"));

            ticket.setAcknowledgedBy(engineer);
            ticket.setStatus(TicketStatus.ACKNOWLEDGED);

            Ticket updatedTicket = ticketRepo.save(ticket);
            return ResponseEntity.ok(updatedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}