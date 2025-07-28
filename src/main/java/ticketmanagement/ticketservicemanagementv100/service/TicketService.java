package ticketmanagement.ticketservicemanagementv100.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.entity.TicketCategory;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final TicketCategoryService categoryService;

    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.CREATED);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket createCustomerTicket(Long customerId, String description, Long categoryId) {
        User customer = userService.findById(customerId);
        if (customer == null || customer.getRole() != UserRole.CUSTOMER) {
            throw new EntityNotFoundException("Customer not found");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle("Ticket from " + customer.getFullName());
        ticket.setDescription(description);
        ticket.setCustomer(customer);
        ticket.setStatus(TicketStatus.CREATED);

        if (categoryId != null) {
            TicketCategory category = categoryService.findById(categoryId);
            ticket.setCategory(category);
        }

        return createTicket(ticket);
    }

    public Ticket updateTicket(Ticket ticket) {
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getTicketsForUser(User user) {
        if (user.getRole() == UserRole.CUSTOMER) {
            return ticketRepository.findByCustomer(user);
        } else {
            return ticketRepository.findAll(); // Engineers can see all tickets
        }
    }

    public List<Ticket> getUnassignedTickets() {
        return ticketRepository.findByAssignedEngineerIsNull();
    }

    public List<Ticket> getAssignedTickets(User engineer) {
        return ticketRepository.findByAssignedEngineer(engineer);
    }

    public Ticket acknowledgeTicket(Long ticketId, User engineer) {
        Ticket ticket = findById(ticketId);
        if (ticket != null && ticket.getAssignedEngineer() == null) {
            ticket.setAssignedEngineer(engineer);
            ticket.setStatus(TicketStatus.ACKNOWLEDGED);
            return updateTicket(ticket);
        }
        throw new EntityNotFoundException("Ticket not found or already assigned");
    }

    public Ticket acknowledgeTicket(Long ticketId, String engineerUsername) {
        User engineer = userService.findByUsername(engineerUsername);
        if (engineer != null && engineer.getRole() == UserRole.ENGINEER) {
            return acknowledgeTicket(ticketId, engineer);
        }
        throw new EntityNotFoundException("Engineer not found");
    }

    public Ticket reassignTicket(Long ticketId, Long newEngineerId, User currentEngineer) {
        if (currentEngineer.getRole() != UserRole.ENGINEER) {
            throw new RuntimeException("Only engineers can reassign tickets");
        }

        Ticket ticket = findById(ticketId);
        User newEngineer = userService.findById(newEngineerId);

        ticket.setAssignedEngineer(newEngineer);
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}