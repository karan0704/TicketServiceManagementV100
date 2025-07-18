package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerResponseDTO {
    private Long id;
    private String username;
}