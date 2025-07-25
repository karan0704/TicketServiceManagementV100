package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticketmanagement.ticketservicemanagementv100.entity.*;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import ticketmanagement.ticketservicemanagementv100.service.*;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    private final TicketService ticketService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final UserService userService;
    private final TicketCategoryService categoryService;

    @PostMapping("/tickets")
    public ResponseEntity<Map<String, Object>> createTicket(@RequestBody Ticket ticket, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            ticket.setCustomer(currentUser);
            Ticket createdTicket = ticketService.createTicket(ticket);
            response.put("success", true);
            response.put("ticket", createdTicket);
            response.put("message", "Ticket created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/tickets")
    public ResponseEntity<Map<String, Object>> getMyTickets(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            List<Ticket> tickets = ticketService.getTicketsForUser(currentUser);
            response.put("success", true);
            response.put("tickets", tickets);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/tickets/search")
    public ResponseEntity<Map<String, Object>> searchMyTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Long categoryId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            List<Ticket> tickets = ticketService.searchTickets(status, categoryId, null, currentUser);
            response.put("success", true);
            response.put("tickets", tickets);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error searching tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long ticketId, @RequestBody Comment comment, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Ticket ticket = ticketService.findById(ticketId);
            if (ticket == null || !ticket.getCustomer().getId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Ticket not found or access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            comment.setTicket(ticket);
            comment.setAuthor(currentUser);
            Comment savedComment = commentService.addComment(comment);
            response.put("success", true);
            response.put("comment", savedComment);
            response.put("message", "Comment added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/tickets/{ticketId}/attachments")
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String comment,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Ticket ticket = ticketService.findById(ticketId);
            if (ticket == null || !ticket.getCustomer().getId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Ticket not found or access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Attachment attachment = attachmentService.saveAttachment(file, ticket, comment, currentUser);
            response.put("success", true);
            response.put("attachment", attachment);
            response.put("message", "File uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error uploading file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();
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

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody User updatedUser, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setEmail(updatedUser.getEmail());
            currentUser.setPhoneNumber(updatedUser.getPhoneNumber());

            User savedUser = userService.updateUser(currentUser);
            session.setAttribute("currentUser", savedUser);
            response.put("success", true);
            response.put("user", savedUser);
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}