package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.LoginRequestDTO;
import ticketmanagement.ticketservicemanagementv100.dto.LoginResponseDTO;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO credentials) {
        try {
            User user = userService.authenticate(credentials.getUsername(), credentials.getPassword());
            if (user != null) {
                LoginResponseDTO response = new LoginResponseDTO();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setFullName(user.getFullName());
                response.setRole(user.getRole().toString());
                response.setEmail(user.getEmail());
                response.setPhoneNumber(user.getPhoneNumber());
                response.setAddress(user.getAddress());
                response.setCompanyName(user.getCompanyName());
                response.setSpecialization(user.getSpecialization());
                response.setIsDefaultEngineer(user.getIsDefaultEngineer());
                response.setSuccess(true);
                response.setMessage("Login successful");
                return ResponseEntity.ok(response);
            }

            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDTO> logout() {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setSuccess(true);
        response.setMessage("Logout successful - clear localStorage");
        return ResponseEntity.ok(response);
    }
}