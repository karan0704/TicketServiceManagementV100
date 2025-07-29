package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticketmanagement.ticketservicemanagementv100.dto.TicketUpdateDTO;
import ticketmanagement.ticketservicemanagementv100.dto.UserRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.entity.Attachment;
import ticketmanagement.ticketservicemanagementv100.entity.Comment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.service.AttachmentService;
import ticketmanagement.ticketservicemanagementv100.service.CommentService;
import ticketmanagement.ticketservicemanagementv100.service.TicketService;
import ticketmanagement.ticketservicemanagementv100.service.UserService;
import ticketmanagement.ticketservicemanagementv100.util.SecurityUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/engineer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EngineerController {
    private final TicketService ticketService;
    private final UserService userService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;

    @GetMapping("/tickets/unassigned")
    public ResponseEntity<List<Ticket>> getUnassignedTickets(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        List<Ticket> tickets = ticketService.getUnassignedTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/tickets/assigned")
    public ResponseEntity<List<Ticket>> getAssignedTickets(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User engineer = SecurityUtil.validateAndGetUser(userId, role, username);

        List<Ticket> tickets = ticketService.getAssignedTickets(engineer);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/tickets/{id}/acknowledge")
    public ResponseEntity<Ticket> acknowledgeTicket(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User engineer = SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.acknowledgeTicket(id, engineer);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/tickets/{id}/update")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketUpdateDTO dto,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(id);
        if (dto.getStatus() != null) ticket.setStatus(dto.getStatus());
        if (dto.getTentativeResolutionDate() != null)
            ticket.setTentativeResolutionDate(dto.getTentativeResolutionDate());
        if (dto.getDescription() != null) ticket.setDescription(dto.getDescription());

        Ticket updatedTicket = ticketService.updateTicket(ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/customers")
    public ResponseEntity<User> createCustomer(
            @RequestBody UserRegistrationDTO dto,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        User customer = new User();
        customer.setUsername(dto.getUsername());
        customer.setPassword(dto.getPassword());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setAddress(dto.getAddress());
        customer.setCompanyName(dto.getCompanyName());
        customer.setRole(UserRole.CUSTOMER);
        customer.setIsDefaultEngineer(false);

        User createdCustomer = userService.createUser(customer);
        return ResponseEntity.ok(createdCustomer);
    }

    @GetMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<List<Comment>> getTicketComments(
            @PathVariable Long ticketId,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(ticketId);
        List<Comment> comments = commentService.getCommentsByTicket(ticket);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<User>> getAllCustomers(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        List<User> customers = userService.findByRole(UserRole.CUSTOMER);
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/tickets/{ticketId}/attachments")
    public ResponseEntity<Attachment> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        User engineer = SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Attachment attachment = attachmentService.saveAttachment(file, ticket, comment, engineer);
            return ResponseEntity.ok(attachment);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable Long attachmentId,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        try {
            Attachment attachment = attachmentService.findById(attachmentId);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = attachmentService.downloadAttachment(attachmentId);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .header("Content-Type", attachment.getFileType())
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/tickets/{ticketId}/attachments")
    public ResponseEntity<List<Attachment>> getTicketAttachments(
            @PathVariable Long ticketId,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "ENGINEER");
        SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(ticketId);
        List<Attachment> attachments = attachmentService.getAttachmentsByTicket(ticket);
        return ResponseEntity.ok(attachments);
    }

}