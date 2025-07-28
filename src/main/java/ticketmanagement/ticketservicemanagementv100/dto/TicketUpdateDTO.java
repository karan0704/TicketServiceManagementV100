package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateDTO {
    private String description;
    private TicketStatus status;
    private Long categoryId;
    private Long engineerId;
    private LocalDate tentativeResolutionDate;
    private String customerCommentOnTicket;
    private String engineerCommentOnTicket;
}