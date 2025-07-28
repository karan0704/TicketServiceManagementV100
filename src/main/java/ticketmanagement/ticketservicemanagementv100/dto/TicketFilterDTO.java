package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

/**
 * DTO for filtering/searching tickets
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketFilterDTO {
    private TicketStatus status;        // filter by status
    private Long categoryId;            // filter by category
    private String customerName;        // search by customer name (engineers only)
    private Long assignedEngineerId;    // filter by assigned engineer
    private Boolean unassignedOnly;     // show only unassigned tickets
}