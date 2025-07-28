package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for profile updates - role-specific field updates
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {
    private String fullName;            // All users can update
    private String email;               // All users can update
    private String phoneNumber;         // All users can update

    // Customer-specific fields
    private String address;             // customers only
    private String companyName;         // customers only

    // Engineer-specific fields
    private String specialization;      // engineers only

    // Password change
    private String currentPassword;     // for password verification
    private String newPassword;         // optional password change
}