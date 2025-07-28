package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating tickets - customers create tickets
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreationDTO {
    private String description;         // Required
    private Long categoryId;            // Optional category selection
}