package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for login responses - returns user data for frontend storage
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;                    // User ID for headers
    private String username;
    private String fullName;
    private String role;
    private String email;
    private String phoneNumber;
    private String address;             // for customers
    private String companyName;         // for customers
    private String specialization;      // for engineers
    private Boolean isDefaultEngineer;  // for admin check
    private boolean success;
    private String message;
}