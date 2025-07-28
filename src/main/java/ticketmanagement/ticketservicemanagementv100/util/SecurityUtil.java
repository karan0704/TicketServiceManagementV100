package ticketmanagement.ticketservicemanagementv100.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.exception.UnauthorizedException;
import ticketmanagement.ticketservicemanagementv100.service.UserService;

@Component
public class SecurityUtil {

    private static UserService userService;

    @Autowired
    public SecurityUtil(UserService userService) {
        SecurityUtil.userService = userService;
    }

    public static void validateUserAccess(Long userId, String role, String username) {
        User user = userService.findById(userId);
        if (user == null || !user.getRole().toString().equals(role) ||
            !user.getUsername().equals(username)) {
            throw new UnauthorizedException("Invalid user credentials");
        }
    }

    public static void validateRole(String actualRole, String requiredRole) {
        if (!requiredRole.equals(actualRole)) {
            throw new UnauthorizedException("Access denied for role: " + actualRole);
        }
    }

    public static User validateAndGetUser(Long userId, String role, String username) {
        validateUserAccess(userId, role, username);
        return userService.findById(userId);
    }
}