package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;

/**
 * DTO for user registration - used by admin engineers to create accounts
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;

    // Customer-specific fields
    private String address;             // for customers only
    private String companyName;         // for customers only

    // Engineer-specific fields
    private String specialization;      // for engineers only
    private Boolean isDefaultEngineer;  // for admin engineers only

    private UserRole role;              // CUSTOMER or ENGINEER
}