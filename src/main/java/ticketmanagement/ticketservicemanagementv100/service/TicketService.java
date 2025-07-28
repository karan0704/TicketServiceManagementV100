package ticketmanagement.ticketservicemanagementv100.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserService userService;

    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.CREATED);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    // ADD THIS METHOD for TicketController
    public Ticket createTicket(Long customerId, String description, Long engineerId) {
        User customer = userService.findById(customerId);
        if (customer == null || customer.getRole() != UserRole.CUSTOMER) {
            throw new EntityNotFoundException("Customer not found");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle("Ticket from " + customer.getFullName()); // Auto-generate title
        ticket.setDescription(description);
        ticket.setCustomer(customer);
        ticket.setStatus(TicketStatus.CREATED);

        if (engineerId != null) {
            User engineer = userService.findById(engineerId);
            if (engineer != null && engineer.getRole() == UserRole.ENGINEER) {
                ticket.setAssignedEngineer(engineer);
                ticket.setStatus(TicketStatus.ACKNOWLEDGED);
            }
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
            // For engineers, return both assigned and unassigned tickets
            List<Ticket> assignedTickets = ticketRepository.findByAssignedEngineer(user);
            List<Ticket> unassignedTickets = ticketRepository.findByAssignedEngineerIsNull();
            return Stream.concat(assignedTickets.stream(), unassignedTickets.stream()).collect(Collectors.toList());
        }
    }

    public List<Ticket> getUnassignedTickets() {
        return ticketRepository.findByAssignedEngineerIsNull();
    }

    public List<Ticket> getAssignedTickets(User engineer) {
        return ticketRepository.findByAssignedEngineer(engineer);
    }

    /*
    public Ticket acknowledgeTicket(Long ticketId, User engineer) {
        Ticket ticket = findById(ticketId);
        if (ticket != null && ticket.getAssignedEngineer() == null) {
            ticket.setAssignedEngineer(engineer);
            ticket.setStatus(TicketStatus.ACKNOWLEDGED);
            return updateTicket(ticket);
        }
        return null;
    }*/
    public Ticket acknowledgeTicket(Long ticketId, String engineerUsername) {
        User engineer = userService.findByUsername(engineerUsername);
        if (engineer != null && engineer.getRole() == UserRole.ENGINEER) {
            return acknowledgeTicket(ticketId, engineer);
        }
        return null;
    }

    public Ticket acknowledgeTicketByUser(Long ticketId, User engineer) {
        Ticket ticket = findById(ticketId);
        if (ticket != null && ticket.getAssignedEngineer() == null) {
            ticket.setAssignedEngineer(engineer);
            ticket.setStatus(TicketStatus.ACKNOWLEDGED);
            return updateTicket(ticket);
        }
        return null;
    }


    public List<Ticket> searchTickets(TicketStatus status, Long categoryId, String customerName, User currentUser) {
        if (currentUser.getRole() == UserRole.CUSTOMER) {
            return ticketRepository.findCustomerTicketsWithFilters(currentUser, status, categoryId);
        } else {
            return ticketRepository.findTicketsWithFilters(status, categoryId, customerName);
        }
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
