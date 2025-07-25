package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.LoginRequest;
import ticketmanagement.ticketservicemanagementv100.dto.LoginResponseDTO;
import ticketmanagement.ticketservicemanagementv100.entity.*;
import ticketmanagement.ticketservicemanagementv100.service.LoginService;

import java.util.Optional;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = loginService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            LoginResponseDTO response = new LoginResponseDTO(
                    user.getUsername(),
                    user.getRole().name(),
                    "Login successful"
            );
            return ResponseEntity.ok(response);
        }

        // If authentication fails
        LoginResponseDTO errorResponse = new LoginResponseDTO(
                loginRequest.getUsername(),
                "UNKNOWN",
                "Invalid username or password"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}