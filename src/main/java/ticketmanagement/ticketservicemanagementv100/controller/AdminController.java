package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.UserRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.CategoryDTO;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.entity.TicketCategory;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.service.UserService;
import ticketmanagement.ticketservicemanagementv100.service.TicketCategoryService;
import ticketmanagement.ticketservicemanagementv100.util.SecurityUtil;
import ticketmanagement.ticketservicemanagementv100.util.RoleValidator;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final UserService userService;
    private final TicketCategoryService categoryService;

    @PostMapping("/engineers")
    public ResponseEntity<User> createEngineer(
            @RequestBody UserRegistrationDTO dto,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User admin = SecurityUtil.validateAndGetUser(userId, role, username);

        if (!RoleValidator.isAdminEngineer(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User engineer = new User();
        engineer.setUsername(dto.getUsername());
        engineer.setPassword(dto.getPassword());
        engineer.setFullName(dto.getFullName());
        engineer.setEmail(dto.getEmail());
        engineer.setPhoneNumber(dto.getPhoneNumber());
        engineer.setSpecialization(dto.getSpecialization());
        engineer.setRole(UserRole.ENGINEER);
        engineer.setIsDefaultEngineer(dto.getIsDefaultEngineer() != null ? dto.getIsDefaultEngineer() : false);

        User createdEngineer = userService.createUser(engineer);
        return ResponseEntity.ok(createdEngineer);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User admin = SecurityUtil.validateAndGetUser(userId, role, username);

        if (!RoleValidator.isAdminEngineer(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User admin = SecurityUtil.validateAndGetUser(userId, role, username);

        if (!RoleValidator.isAdminEngineer(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories")
    public ResponseEntity<TicketCategory> createCategory(
            @RequestBody CategoryDTO dto,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User admin = SecurityUtil.validateAndGetUser(userId, role, username);

        if (!RoleValidator.isAdminEngineer(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TicketCategory category = new TicketCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        TicketCategory createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<TicketCategory>> getAllCategories(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User admin = SecurityUtil.validateAndGetUser(userId, role, username);

        if (!RoleValidator.isAdminEngineer(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TicketCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}