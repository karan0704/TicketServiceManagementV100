package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticketmanagement.ticketservicemanagementv100.dto.CommentDTO;
import ticketmanagement.ticketservicemanagementv100.dto.ProfileUpdateDTO;
import ticketmanagement.ticketservicemanagementv100.dto.TicketCreationDTO;
import ticketmanagement.ticketservicemanagementv100.entity.Attachment;
import ticketmanagement.ticketservicemanagementv100.entity.Comment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.service.AttachmentService;
import ticketmanagement.ticketservicemanagementv100.service.CommentService;
import ticketmanagement.ticketservicemanagementv100.service.TicketService;
import ticketmanagement.ticketservicemanagementv100.service.UserService;
import ticketmanagement.ticketservicemanagementv100.util.SecurityUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    private final TicketService ticketService;
    private final UserService userService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;

    @PostMapping("/tickets")
    public ResponseEntity<Ticket> createTicket(
            @RequestBody TicketCreationDTO dto,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.createCustomerTicket(customer.getId(), dto.getDescription(), dto.getCategoryId());
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getMyTickets(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        List<Ticket> tickets = ticketService.getTicketsForUser(customer);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/tickets/{ticketId}/attachments")
    public ResponseEntity<Attachment> uploadAttachment(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        // Verify ticket belongs to customer
        Ticket ticket = ticketService.findById(ticketId);
        if (!ticket.getCustomer().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Attachment attachment = attachmentService.saveAttachment(file, ticket, comment, customer);
            return ResponseEntity.ok(attachment);
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

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(ticketId);
        if (!ticket.getCustomer().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Attachment> attachments = attachmentService.getAttachmentsByTicket(ticket);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable Long attachmentId,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        try {
            Attachment attachment = attachmentService.findById(attachmentId);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if customer owns the ticket
            if (!attachment.getTicket().getCustomer().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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



    @PostMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long ticketId,
            @RequestBody CommentDTO commentDTO,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(ticketId);
        if (!ticket.getCustomer().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTicket(ticket);
        comment.setAuthor(customer);

        Comment savedComment = commentService.addComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestBody ProfileUpdateDTO dto,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        // Update customer-specific fields
        if (dto.getFullName() != null) customer.setFullName(dto.getFullName());
        if (dto.getEmail() != null) customer.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) customer.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) customer.setAddress(dto.getAddress());
        if (dto.getCompanyName() != null) customer.setCompanyName(dto.getCompanyName());

        User updatedUser = userService.updateUser(customer);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<List<Comment>> getTicketComments(
            @PathVariable Long ticketId,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Username") String username) {

        SecurityUtil.validateRole(role, "CUSTOMER");
        User customer = SecurityUtil.validateAndGetUser(userId, role, username);

        Ticket ticket = ticketService.findById(ticketId);
        if (!ticket.getCustomer().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Comment> comments = commentService.getCommentsByTicket(ticket);
        return ResponseEntity.ok(comments);
    }

}