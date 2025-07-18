package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.model.TicketStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateDTO {
    private String description;
    private TicketStatus status;
    private String customerCommentOnTicket;
    private String engineerCommentOnTicket;
    private LocalDate tentativeResolutionDate;
    private Long engineerId; // Optional reassignment
}