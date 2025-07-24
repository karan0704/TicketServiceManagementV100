package ticketmanagement.ticketservicemanagementv100.controller;

import ticketmanagement.ticketservicemanagementv100.entity.*;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final UserService userService;
    private final TicketCategoryService categoryService;
    
    @PostMapping("/customers")
    public ResponseEntity<Map<String, Object>> createCustomer(@RequestBody User customer, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !userService.isDefaultEngineer(currentUser)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            customer.setRole(UserRole.CUSTOMER);
            customer.setIsDefaultEngineer(false);
            
            User createdCustomer = userService.createUser(customer);
            response.put("success", true);
            response.put("customer", createdCustomer);
            response.put("message", "Customer created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/engineers")
    public ResponseEntity<Map<String, Object>> createEngineer(@RequestBody User engineer, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !userService.isDefaultEngineer(currentUser)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            engineer.setRole(UserRole.ENGINEER);
            engineer.setIsDefaultEngineer(false);
            
            User createdEngineer = userService.createUser(engineer);
            response.put("success", true);
            response.put("engineer", createdEngineer);
            response.put("message", "Engineer created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getAllCustomers(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !userService.isDefaultEngineer(currentUser)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            List<User> customers = userService.findByRole(UserRole.CUSTOMER);
            response.put("success", true);
            response.put("customers", customers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching customers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody TicketCategory category, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !userService.isDefaultEngineer(currentUser)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            TicketCategory createdCategory = categoryService.createCategory(category);
            response.put("success", true);
            response.put("category", createdCategory);
            response.put("message", "Category created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !userService.isDefaultEngineer(currentUser)) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            List<TicketCategory> categories = categoryService.getAllCategories();
            response.put("success", true);
            response.put("categories", categories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching categories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
