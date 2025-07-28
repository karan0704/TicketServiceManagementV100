package ticketmanagement.ticketservicemanagementv100.util;

import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;

public class RoleValidator {

    public static boolean canAccessTicket(User user, Ticket ticket) {
        if (user.getRole() == UserRole.CUSTOMER) {
            return ticket.getCustomer().getId().equals(user.getId());
        }
        return user.getRole() == UserRole.ENGINEER; // Engineers can access all
    }

    public static boolean isCustomer(User user) {
        return user.getRole() == UserRole.CUSTOMER;
    }

    public static boolean isEngineer(User user) {
        return user.getRole() == UserRole.ENGINEER;
    }

    public static boolean isAdminEngineer(User user) {
        return user.getRole() == UserRole.ENGINEER &&
               Boolean.TRUE.equals(user.getIsDefaultEngineer());
    }
}