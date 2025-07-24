package ticketmanagement.ticketservicemanagementv100.repository;

import ticketmanagement.ticketservicemanagementv100.entity.Attachment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTicket(Ticket ticket);
}
