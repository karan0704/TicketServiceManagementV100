package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

import java.time.LocalDate;

/**
 * DTO for updating tickets - engineers update ticket details
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateDTO {
    private String description;
    private TicketStatus status;
    private Long categoryId;
    private Long engineerId;                   // for reassignment
    private LocalDate tentativeResolutionDate; // Changed from LocalDateTime
    private String engineerComment;
}