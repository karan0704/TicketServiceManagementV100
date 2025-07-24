package ticketmanagement.ticketservicemanagementv100.repository;

import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCustomer(User customer);
    List<Ticket> findByAssignedEngineer(User engineer);
    List<Ticket> findByAssignedEngineerIsNull();
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByCategory_Id(Long categoryId);
    
    @Query("SELECT t FROM Ticket t WHERE t.customer.fullName LIKE %:customerName%")
    List<Ticket> findByCustomerNameContaining(@Param("customerName") String customerName);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
           "(:customerName IS NULL OR t.customer.fullName LIKE %:customerName%)")
    List<Ticket> findTicketsWithFilters(
        @Param("status") TicketStatus status,
        @Param("categoryId") Long categoryId,
        @Param("customerName") String customerName
    );
    
    @Query("SELECT t FROM Ticket t WHERE t.customer = :customer AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Ticket> findCustomerTicketsWithFilters(
        @Param("customer") User customer,
        @Param("status") TicketStatus status,
        @Param("categoryId") Long categoryId
    );
}
