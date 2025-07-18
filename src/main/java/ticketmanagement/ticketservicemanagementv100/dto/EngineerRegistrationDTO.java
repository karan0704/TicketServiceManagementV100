package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a new Engineer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngineerRegistrationDTO {
    private String username;
    private String password;
}