package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticketmanagement.ticketservicemanagementv100.dto.LoginRequest;
import ticketmanagement.ticketservicemanagementv100.model.User;
import ticketmanagement.ticketservicemanagementv100.service.LoginService;

import java.util.Collections;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = loginService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (user != null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Login successful for " + user.getUsername() + " with role " + user.getRole()));
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }
}
