package ticketmanagement.ticketservicemanagementv100.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.model.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
