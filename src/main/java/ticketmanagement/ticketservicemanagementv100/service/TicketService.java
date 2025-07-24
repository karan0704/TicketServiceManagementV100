package ticketmanagement.ticketservicemanagementv100.service;

import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    
    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.CREATED);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
    
    public Ticket updateTicket(Ticket ticket) {
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
    
    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }
    
    public List<Ticket> getTicketsForUser(User user) {
        if (user.getRole() == UserRole.CUSTOMER) {
            return ticketRepository.findByCustomer(user);
        } else {
            // For engineers, return both assigned and unassigned tickets
            List<Ticket> assignedTickets = ticketRepository.findByAssignedEngineer(user);
            List<Ticket> unassignedTickets = ticketRepository.findByAssignedEngineerIsNull();
            return Stream.concat(assignedTickets.stream(), unassignedTickets.stream())
                        .collect(Collectors.toList());
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
