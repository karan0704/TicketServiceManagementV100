package ticketmanagement.ticketservicemanagementv100.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/engineer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EngineerController {
    private final TicketService ticketService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final UserService userService;
    private final TicketCategoryService categoryService;

    @GetMapping("/tickets")
    public ResponseEntity<Map<String, Object>> getAllTickets(HttpSession session) {
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

    @GetMapping("/tickets/unassigned")
    public ResponseEntity<Map<String, Object>> getUnassignedTickets() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ticket> tickets = ticketService.getUnassignedTickets();
            response.put("success", true);
            response.put("tickets", tickets);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching unassigned tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/tickets/{ticketId}/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeTicket(@PathVariable Long ticketId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Ticket ticket = ticketService.acknowledgeTicket(ticketId, currentUser);
            if (ticket != null) {
                response.put("success", true);
                response.put("ticket", ticket);
                response.put("message", "Ticket acknowledged successfully");
                return ResponseEntity.ok(response);
            }
            response.put("success", false);
            response.put("message", "Unable to acknowledge ticket");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error acknowledging ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/tickets/{ticketId}")
    public ResponseEntity<Map<String, Object>> updateTicket(@PathVariable Long ticketId, @RequestBody Ticket updatedTicket, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Ticket ticket = ticketService.findById(ticketId);
            if (ticket == null) {
                response.put("success", false);
                response.put("message", "Ticket not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Update allowed fields
            ticket.setStatus(updatedTicket.getStatus());
            ticket.setDescription(updatedTicket.getDescription());
            ticket.setTentativeResolutionDate(updatedTicket.getTentativeResolutionDate());
            if (updatedTicket.getAssignedEngineer() != null) {
                ticket.setAssignedEngineer(updatedTicket.getAssignedEngineer());
            }

            Ticket savedTicket = ticketService.updateTicket(ticket);
            response.put("success", true);
            response.put("ticket", savedTicket);
            response.put("message", "Ticket updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<Map<String, Object>> deleteTicket(@PathVariable Long ticketId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ticketService.deleteTicket(ticketId);
            response.put("success", true);
            response.put("message", "Ticket deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/tickets/search")
    public ResponseEntity<Map<String, Object>> searchTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String customerName,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            List<Ticket> tickets = ticketService.searchTickets(status, categoryId, customerName, currentUser);
            response.put("success", true);
            response.put("tickets", tickets);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error searching tickets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/engineers")
    public ResponseEntity<Map<String, Object>> getAllEngineers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> engineers = userService.findByRole(UserRole.ENGINEER);
            response.put("success", true);
            response.put("engineers", engineers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching engineers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
