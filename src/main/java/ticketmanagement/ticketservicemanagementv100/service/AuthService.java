package ticketmanagement.ticketservicemanagementv100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    public User authenticate(String username, String password) {
        return userService.authenticate(username, password);
    }
}