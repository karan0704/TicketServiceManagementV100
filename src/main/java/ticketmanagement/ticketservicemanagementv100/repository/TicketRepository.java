package ticketmanagement.ticketservicemanagementv100.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.model.Ticket;
import ticketmanagement.ticketservicemanagementv100.model.TicketStatus;

import java.util.List;


public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // TicketRepository.java
    List<Ticket> findByCreatedById(Long customerId);

    List<Ticket> findByAcknowledgedById(Long engineerId);

    List<Ticket> findByStatus(TicketStatus status);

}
