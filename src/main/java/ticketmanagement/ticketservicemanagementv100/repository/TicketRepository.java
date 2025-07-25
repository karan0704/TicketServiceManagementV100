package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCustomer(User customer);

    List<Ticket> findByAssignedEngineer(User engineer);

    List<Ticket> findByAssignedEngineerIsNull();

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByCategory_Id(Long categoryId);

}
