package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Ticket> findByCategoryId(Long categoryId);

    // ADD THESE MISSING METHODS:
    @Query("SELECT t FROM Ticket t WHERE t.customer = :user AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Ticket> findCustomerTicketsWithFilters(@Param("user") User user,
                                                @Param("status") TicketStatus status,
                                                @Param("categoryId") Long categoryId);   // ✅ ADD

    @Query("SELECT t FROM Ticket t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
            "(:customerName IS NULL OR LOWER(t.customer.fullName) LIKE LOWER(CONCAT('%', :customerName, '%')))")
    List<Ticket> findTicketsWithFilters(@Param("status") TicketStatus status,
                                        @Param("categoryId") Long categoryId,
                                        @Param("customerName") String customerName);   // ✅ ADD

    // ADD CONVENIENCE METHODS:
    List<Ticket> findByCreatedById(Long customerId);        // ✅ ADD

    List<Ticket> findByAcknowledgedById(Long engineerId);   // ✅ ADD
}
