import os
import json
from pathlib import Path

class CompleteSpringBootProjectGenerator:
    def __init__(self, base_path):
        self.base_path = Path(base_path)
        self.java_base = self.base_path / "src/main/java/ticketmanagement/ticketservicemanagementv100"
        self.resources_base = self.base_path / "src/main/resources"
        self.test_base = self.base_path / "src/test/java/ticketmanagement/ticketservicemanagementv100"

    def create_complete_directory_structure(self):
        """Create complete directory structure"""
        directories = [
            # Main Java packages
            self.java_base / "controller",
            self.java_base / "dto",
            self.java_base / "entity",
            self.java_base / "service",
            self.java_base / "repository",
            self.java_base / "util",
            self.java_base / "exception",
            self.java_base / "config",
            self.java_base / "enums",

            # Resources
            self.resources_base / "static/css",
            self.resources_base / "static/js",
            self.resources_base / "static/uploads",
            self.resources_base / "templates",

            # Test directories
            self.test_base / "controller",
            self.test_base / "service",
            self.test_base / "repository",

            # Maven structure
            self.base_path / ".mvn/wrapper",
            self.base_path / "target/classes"
        ]

        for directory in directories:
            directory.mkdir(parents=True, exist_ok=True)
            print(f"✅ Created directory: {directory}")

    def write_file(self, file_path, content):
        """Write content to file"""
        file_path.parent.mkdir(parents=True, exist_ok=True)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"✅ Created file: {file_path}")

    def generate_main_application_class(self):
        """Generate main Spring Boot application class"""
        main_app = '''package ticketmanagement.ticketservicemanagementv100;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicketServiceManagementV100Application {
    public static void main(String[] args) {
        SpringApplication.run(TicketServiceManagementV100Application.class, args);
    }
}'''

        self.write_file(self.java_base / "TicketServiceManagementV100Application.java", main_app)

    def generate_enums(self):
        """Generate all enum classes"""

        # UserRole.java
        user_role_enum = '''package ticketmanagement.ticketservicemanagementv100.enums;

public enum UserRole {
    CUSTOMER, ENGINEER
}'''

        # TicketStatus.java
        ticket_status_enum = '''package ticketmanagement.ticketservicemanagementv100.enums;

public enum TicketStatus {
    CREATED, ACKNOWLEDGED, IN_PROGRESS, CLOSED
}'''

        self.write_file(self.java_base / "enums/UserRole.java", user_role_enum)
        self.write_file(self.java_base / "enums/TicketStatus.java", ticket_status_enum)

    def generate_complete_entities(self):
        """Generate all entity classes"""

        # Updated User.java
        user_entity = '''package ticketmanagement.ticketservicemanagementv100.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;
    private String address;                        // For customers
    private String specialization;                 // For engineers
    private String companyName;                    // 🔥 NEW - For customers
    private Boolean isDefaultEngineer = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> createdTickets = new ArrayList<>();

    @OneToMany(mappedBy = "assignedEngineer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> assignedTickets = new ArrayList<>();
}'''

        # Updated Ticket.java
        ticket_entity = '''package ticketmanagement.ticketservicemanagementv100.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.CREATED;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"createdTickets", "assignedTickets"})
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_engineer_id")
    @JsonIgnoreProperties({"createdTickets", "assignedTickets"})
    private User assignedEngineer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private TicketCategory category;

    // 🔄 CHANGED: LocalDateTime → LocalDate
    private LocalDate tentativeResolutionDate;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attachment> attachments;
}'''

        # Comment.java
        comment_entity = '''package ticketmanagement.ticketservicemanagementv100.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonIgnoreProperties({"comments", "attachments", "customer", "assignedEngineer"})
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties({"createdTickets", "assignedTickets"})
    private User author;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}'''

        # Attachment.java
        attachment_entity = '''package ticketmanagement.ticketservicemanagementv100.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private String fileType;

    private Long fileSize;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonIgnoreProperties({"comments", "attachments", "customer", "assignedEngineer"})
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploaded_by", nullable = false)
    @JsonIgnoreProperties({"createdTickets", "assignedTickets"})
    private User uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}'''

        # TicketCategory.java
        category_entity = '''package ticketmanagement.ticketservicemanagementv100.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ticket_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets;
}'''

        entity_files = [
            ("User.java", user_entity),
            ("Ticket.java", ticket_entity),
            ("Comment.java", comment_entity),
            ("Attachment.java", attachment_entity),
            ("TicketCategory.java", category_entity)
        ]

        for filename, content in entity_files:
            self.write_file(self.java_base / f"entity/{filename}", content)

    def generate_complete_dtos(self):
        """Generate all DTO classes"""

        # LoginRequestDTO.java
        login_request_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for login requests - simple header-based authentication
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private String username;
    private String password;
}'''

        # LoginResponseDTO.java
        login_response_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for login responses - returns user data for frontend storage
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;                    // User ID for headers
    private String username;
    private String fullName;
    private String role;
    private String email;
    private String phoneNumber;
    private String address;             // for customers
    private String companyName;         // for customers
    private String specialization;      // for engineers
    private Boolean isDefaultEngineer;  // for admin check
    private boolean success;
    private String message;
}'''

        # UserRegistrationDTO.java
        user_registration_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;

/**
 * DTO for user registration - used by admin engineers to create accounts
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;

    // Customer-specific fields
    private String address;             // for customers only
    private String companyName;         // for customers only

    // Engineer-specific fields
    private String specialization;      // for engineers only
    private Boolean isDefaultEngineer;  // for admin engineers only

    private UserRole role;              // CUSTOMER or ENGINEER
}'''

        # ProfileUpdateDTO.java
        profile_update_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for profile updates - role-specific field updates
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {
    private String fullName;            // All users can update
    private String email;               // All users can update
    private String phoneNumber;         // All users can update

    // Customer-specific fields
    private String address;             // customers only
    private String companyName;         // customers only

    // Engineer-specific fields
    private String specialization;      // engineers only

    // Password change
    private String currentPassword;     // for password verification
    private String newPassword;         // optional password change
}'''

        # TicketCreationDTO.java
        ticket_creation_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating tickets - customers create tickets
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreationDTO {
    private String description;         // Required
    private Long categoryId;            // Optional category selection
}'''

        # TicketUpdateDTO.java
        ticket_update_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

import java.time.LocalDate;

/**
 * DTO for updating tickets - engineers update ticket details
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateDTO {
    private String description;
    private TicketStatus status;
    private Long categoryId;
    private Long engineerId;                   // for reassignment
    private LocalDate tentativeResolutionDate; // Changed from LocalDateTime
    private String engineerComment;
}'''

        # TicketFilterDTO.java
        ticket_filter_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

/**
 * DTO for filtering/searching tickets
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketFilterDTO {
    private TicketStatus status;        // filter by status
    private Long categoryId;            // filter by category
    private String customerName;        // search by customer name (engineers only)
    private Long assignedEngineerId;    // filter by assigned engineer
    private Boolean unassignedOnly;     // show only unassigned tickets
}'''

        # CommentDTO.java
        comment_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for ticket comments - threaded commenting system
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;             // comment text
    private Long ticketId;              // which ticket this comment belongs to
    private Long authorId;              // who wrote the comment
    private String authorName;          // author's full name
    private String authorRole;          // CUSTOMER or ENGINEER
    private LocalDateTime createdAt;    // when comment was created
}'''

        # AttachmentDTO.java
        attachment_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for file attachments with comments
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    private String fileName;            // original file name
    private String fileType;            // MIME type
    private Long fileSize;              // file size in bytes
    private String comment;             // optional comment with attachment
    private Long ticketId;              // which ticket this attachment belongs to
    private Long uploadedById;          // who uploaded the file
    private String uploadedByName;      // uploader's name
    private String uploadedByRole;      // uploader's role
    private LocalDateTime uploadedAt;   // upload timestamp
    private String downloadUrl;         // URL for downloading file
}'''

        # CategoryDTO.java
        category_dto = '''package ticketmanagement.ticketservicemanagementv100.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for ticket categories
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String name;
    private String description;
}'''

        dto_files = [
            ("LoginRequestDTO.java", login_request_dto),
            ("LoginResponseDTO.java", login_response_dto),
            ("UserRegistrationDTO.java", user_registration_dto),
            ("ProfileUpdateDTO.java", profile_update_dto),
            ("TicketCreationDTO.java", ticket_creation_dto),
            ("TicketUpdateDTO.java", ticket_update_dto),
            ("TicketFilterDTO.java", ticket_filter_dto),
            ("CommentDTO.java", comment_dto),
            ("AttachmentDTO.java", attachment_dto),
            ("CategoryDTO.java", category_dto)
        ]

        for filename, content in dto_files:
            self.write_file(self.java_base / f"dto/{filename}", content)

    def generate_complete_repositories(self):
        """Generate all repository interfaces"""

        # UserRepository.java
        user_repository = '''package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
    List<User> findByRole(UserRole role);
    Optional<User> findByIsDefaultEngineerTrue();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByIsDefaultEngineerFalse();
    Optional<User> findByEmail(String email);
}'''

        # TicketRepository.java
        ticket_repository = '''package ticketmanagement.ticketservicemanagementv100.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCustomer(User customer);
    List<Ticket> findByAssignedEngineer(User engineer);
    List<Ticket> findByAssignedEngineerIsNull();
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByCategoryId(Long categoryId);

    @Query("SELECT t FROM Ticket t WHERE t.customer = :user AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Ticket> findCustomerTicketsWithFilters(@Param("user") User user,
                                                @Param("status") TicketStatus status,
                                                @Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Ticket t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
            "(:customerName IS NULL OR LOWER(t.customer.fullName) LIKE LOWER(CONCAT('%', :customerName, '%')))")
    List<Ticket> findTicketsWithFilters(@Param("status") TicketStatus status,
                                        @Param("categoryId") Long categoryId,
                                        @Param("customerName") String customerName);

    List<Ticket> findByCustomerId(Long customerId);
    List<Ticket> findByAssignedEngineerId(Long engineerId);
}'''

        # CommentRepository.java
        comment_repository = '''package ticketmanagement.ticketservicemanagementv100.repository;

import ticketmanagement.ticketservicemanagementv100.entity.Comment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}'''

        # AttachmentRepository.java
        attachment_repository = '''package ticketmanagement.ticketservicemanagementv100.repository;

import ticketmanagement.ticketservicemanagementv100.entity.Attachment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTicket(Ticket ticket);
}'''

        # TicketCategoryRepository.java
        category_repository = '''package ticketmanagement.ticketservicemanagementv100.repository;

import ticketmanagement.ticketservicemanagementv100.entity.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
    Optional<TicketCategory> findByName(String name);
    boolean existsByName(String name);
}'''

        repository_files = [
            ("UserRepository.java", user_repository),
            ("TicketRepository.java", ticket_repository),
            ("CommentRepository.java", comment_repository),
            ("AttachmentRepository.java", attachment_repository),
            ("TicketCategoryRepository.java", category_repository)
        ]

        for filename, content in repository_files:
            self.write_file(self.java_base / f"repository/{filename}", content)

    def generate_complete_services(self):
        """Generate all service classes"""

        # AuthService.java
        auth_service = '''package ticketmanagement.ticketservicemanagementv100.service;

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
}'''

        # UserService.java
        user_service = '''package ticketmanagement.ticketservicemanagementv100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User authenticate(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsernameAndPassword(username, password);
        return optionalUser.orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public User findDefaultEngineer() {
        return userRepository.findByIsDefaultEngineerTrue().orElse(null);
    }

    public boolean isDefaultEngineer(User user) {
        return user != null && Boolean.TRUE.equals(user.getIsDefaultEngineer());
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}'''

        # TicketService.java
        ticket_service = '''package ticketmanagement.ticketservicemanagementv100.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.entity.TicketCategory;
import ticketmanagement.ticketservicemanagementv100.enums.TicketStatus;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final TicketCategoryService categoryService;

    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.CREATED);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket createCustomerTicket(Long customerId, String description, Long categoryId) {
        User customer = userService.findById(customerId);
        if (customer == null || customer.getRole() != UserRole.CUSTOMER) {
            throw new EntityNotFoundException("Customer not found");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle("Ticket from " + customer.getFullName());
        ticket.setDescription(description);
        ticket.setCustomer(customer);
        ticket.setStatus(TicketStatus.CREATED);

        if (categoryId != null) {
            TicketCategory category = categoryService.findById(categoryId);
            ticket.setCategory(category);
        }

        return createTicket(ticket);
    }

    public Ticket updateTicket(Ticket ticket) {
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getTicketsForUser(User user) {
        if (user.getRole() == UserRole.CUSTOMER) {
            return ticketRepository.findByCustomer(user);
        } else {
            return ticketRepository.findAll(); // Engineers can see all tickets
        }
    }

    public List<Ticket> getUnassignedTickets() {
        return ticketRepository.findByAssignedEngineerIsNull();
    }

    public List<Ticket> getAssignedTickets(User engineer) {
        return ticketRepository.findByAssignedEngineer(engineer);
    }

    public Ticket acknowledgeTicket(Long ticketId, User engineer) {
        Ticket ticket = findById(ticketId);
        if (ticket != null && ticket.getAssignedEngineer() == null) {
            ticket.setAssignedEngineer(engineer);
            ticket.setStatus(TicketStatus.ACKNOWLEDGED);
            return updateTicket(ticket);
        }
        throw new EntityNotFoundException("Ticket not found or already assigned");
    }

    public Ticket acknowledgeTicket(Long ticketId, String engineerUsername) {
        User engineer = userService.findByUsername(engineerUsername);
        if (engineer != null && engineer.getRole() == UserRole.ENGINEER) {
            return acknowledgeTicket(ticketId, engineer);
        }
        throw new EntityNotFoundException("Engineer not found");
    }

    public Ticket reassignTicket(Long ticketId, Long newEngineerId, User currentEngineer) {
        if (currentEngineer.getRole() != UserRole.ENGINEER) {
            throw new RuntimeException("Only engineers can reassign tickets");
        }

        Ticket ticket = findById(ticketId);
        User newEngineer = userService.findById(newEngineerId);

        ticket.setAssignedEngineer(newEngineer);
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}'''

        # CommentService.java
        comment_service = '''package ticketmanagement.ticketservicemanagementv100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.entity.Comment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTicket(Ticket ticket) {
        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
}'''

        # AttachmentService.java
        attachment_service = '''package ticketmanagement.ticketservicemanagementv100.service;

import ticketmanagement.ticketservicemanagementv100.entity.Attachment;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final String uploadDir = "uploads/";

    public Attachment saveAttachment(MultipartFile file, Ticket ticket, String comment, User uploadedBy) throws IOException {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.write(filePath, file.getBytes());

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(filePath.toString());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setComment(comment);
        attachment.setTicket(ticket);
        attachment.setUploadedBy(uploadedBy);
        attachment.setUploadedAt(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }

    public List<Attachment> getAttachmentsByTicket(Ticket ticket) {
        return attachmentRepository.findByTicket(ticket);
    }

    public Attachment findById(Long id) {
        return attachmentRepository.findById(id).orElse(null);
    }

    public byte[] downloadAttachment(Long attachmentId) throws IOException {
        Attachment attachment = findById(attachmentId);
        if (attachment != null) {
            Path filePath = Paths.get(attachment.getFilePath());
            return Files.readAllBytes(filePath);
        }
        return null;
    }
}'''

        # TicketCategoryService.java
        category_service = '''package ticketmanagement.ticketservicemanagementv100.service;

import ticketmanagement.ticketservicemanagementv100.entity.TicketCategory;
import ticketmanagement.ticketservicemanagementv100.repository.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCategoryService {
    private final TicketCategoryRepository categoryRepository;

    public TicketCategory createCategory(TicketCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category name already exists");
        }
        return categoryRepository.save(category);
    }

    public TicketCategory updateCategory(TicketCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public TicketCategory findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public TicketCategory findByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    public List<TicketCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}'''

        service_files = [
            ("AuthService.java", auth_service),
            ("UserService.java", user_service),
            ("TicketService.java", ticket_service),
            ("CommentService.java", comment_service),
            ("AttachmentService.java", attachment_service),
            ("TicketCategoryService.java", category_service)
        ]

        for filename, content in service_files:
            self.write_file(self.java_base / f"service/{filename}", content)

    def generate_utility_classes(self):
        """Generate utility classes for header-based authentication"""

        # SecurityUtil.java
        security_util = '''package ticketmanagement.ticketservicemanagementv100.util;

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
}'''

        # RoleValidator.java
        role_validator = '''package ticketmanagement.ticketservicemanagementv100.util;

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
}'''

        self.write_file(self.java_base / "util/SecurityUtil.java", security_util)
        self.write_file(self.java_base / "util/RoleValidator.java", role_validator)

    def generate_exception_classes(self):
        """Generate custom exception classes"""

        # UnauthorizedException.java
        unauthorized_exception = '''package ticketmanagement.ticketservicemanagementv100.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}'''

        # UserNotFoundException.java
        user_not_found_exception = '''package ticketmanagement.ticketservicemanagementv100.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}'''

        # TicketNotFoundException.java
        ticket_not_found_exception = '''package ticketmanagement.ticketservicemanagementv100.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message) {
        super(message);
    }
}'''

        # GlobalExceptionHandler.java
        global_exception_handler = '''package ticketmanagement.ticketservicemanagementv100.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTicketNotFound(TicketNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}'''

        exception_files = [
            ("UnauthorizedException.java", unauthorized_exception),
            ("UserNotFoundException.java", user_not_found_exception),
            ("TicketNotFoundException.java", ticket_not_found_exception),
            ("GlobalExceptionHandler.java", global_exception_handler)
        ]

        for filename, content in exception_files:
            self.write_file(self.java_base / f"exception/{filename}", content)

    def generate_complete_controllers(self):
        """Generate all controller classes"""

        # AuthController.java
        auth_controller = '''package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.LoginRequestDTO;
import ticketmanagement.ticketservicemanagementv100.dto.LoginResponseDTO;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO credentials) {
        try {
            User user = userService.authenticate(credentials.getUsername(), credentials.getPassword());
            if (user != null) {
                LoginResponseDTO response = new LoginResponseDTO();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setFullName(user.getFullName());
                response.setRole(user.getRole().toString());
                response.setEmail(user.getEmail());
                response.setPhoneNumber(user.getPhoneNumber());
                response.setAddress(user.getAddress());
                response.setCompanyName(user.getCompanyName());
                response.setSpecialization(user.getSpecialization());
                response.setIsDefaultEngineer(user.getIsDefaultEngineer());
                response.setSuccess(true);
                response.setMessage("Login successful");
                return ResponseEntity.ok(response);
            }

            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDTO> logout() {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setSuccess(true);
        response.setMessage("Logout successful - clear localStorage");
        return ResponseEntity.ok(response);
    }
}'''

        # CustomerController.java
        customer_controller = '''package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.TicketCreationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.ProfileUpdateDTO;
import ticketmanagement.ticketservicemanagementv100.dto.CommentDTO;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.entity.Comment;
import ticketmanagement.ticketservicemanagementv100.service.TicketService;
import ticketmanagement.ticketservicemanagementv100.service.UserService;
import ticketmanagement.ticketservicemanagementv100.service.CommentService;
import ticketmanagement.ticketservicemanagementv100.util.SecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    private final TicketService ticketService;
    private final UserService userService;
    private final CommentService commentService;

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
}'''

        # EngineerController.java
        engineer_controller = '''package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.dto.TicketUpdateDTO;
import ticketmanagement.ticketservicemanagementv100.dto.UserRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.entity.Ticket;
import ticketmanagement.ticketservicemanagementv100.entity.User;
import ticketmanagement.ticketservicemanagementv100.enums.UserRole;
import ticketmanagement.ticketservicemanagementv100.service.TicketService;
import ticketmanagement.ticketservicemanagementv100.service.UserService;
import ticketmanagement.ticketservicemanagementv100.util.SecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/engineer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EngineerController {
    private final TicketService ticketService;
    private final UserService userService;

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
        if (dto.getTentativeResolutionDate() != null) ticket.setTentativeResolutionDate(dto.getTentativeResolutionDate());
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
}'''

        # AdminController.java
        admin_controller = '''package ticketmanagement.ticketservicemanagementv100.controller;

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
}'''

        # PublicController.java
        public_controller = '''package ticketmanagement.ticketservicemanagementv100.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticketmanagement.ticketservicemanagementv100.entity.TicketCategory;
import ticketmanagement.ticketservicemanagementv100.service.TicketCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicController {
    private final TicketCategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<TicketCategory>> getCategories() {
        List<TicketCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Application is running");
    }
}'''

        controller_files = [
            ("AuthController.java", auth_controller),
            ("CustomerController.java", customer_controller),
            ("EngineerController.java", engineer_controller),
            ("AdminController.java", admin_controller),
            ("PublicController.java", public_controller)
        ]

        for filename, content in controller_files:
            self.write_file(self.java_base / f"controller/{filename}", content)

    def generate_config_classes(self):
        """Generate configuration classes"""

        # CorsConfig.java
        cors_config = '''package ticketmanagement.ticketservicemanagementv100.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}'''

        self.write_file(self.java_base / "config/CorsConfig.java", cors_config)

    def generate_maven_files(self):
        """Generate Maven configuration files"""

        # pom.xml
        pom_xml = '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>ticket-management</groupId>
    <artifactId>TicketServiceManagementV100</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>TicketServiceManagementV100</name>
    <description>Complete Ticket Service Management System</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Utility -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- File Upload -->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.11.0</version>
        </dependency>

        <!-- Development -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>'''

        self.write_file(self.base_path / "pom.xml", pom_xml)

    def generate_application_properties(self):
        """Generate application.properties"""

        app_properties = '''spring.application.name=ticket-service-management-v100

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ticketdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root@1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Server Configuration
server.port=8080

# CORS Configuration
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

# Logging Configuration
logging.level.ticketmanagement.ticketservicemanagementv100=DEBUG
logging.level.org.springframework.web=DEBUG'''

        self.write_file(self.resources_base / "application.properties", app_properties)

    def generate_basic_frontend_files(self):
        """Generate basic HTML/CSS/JS files"""

        # index.html
        index_html = '''<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket Management System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Ticket Management System</h1>
        <div class="login-form">
            <h2>Login</h2>
            <form id="loginForm">
                <input type="text" id="username" placeholder="Username" required>
                <input type="password" id="password" placeholder="Password" required>
                <button type="submit">Login</button>
            </form>
        </div>
    </div>
    <script src="js/app.js"></script>
</body>
</html>'''

        # style.css
        style_css = '''body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 20px;
    background-color: #f4f4f4;
}

.container {
    max-width: 800px;
    margin: 0 auto;
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.login-form {
    max-width: 400px;
    margin: 20px auto;
}

.login-form input {
    width: 100%;
    padding: 10px;
    margin: 10px 0;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
}

.login-form button {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.login-form button:hover {
    background-color: #0056b3;
}'''

        # app.js
        app_js = '''// Simple header-based authentication
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (data.success) {
            // Store user data in localStorage
            localStorage.setItem('currentUser', JSON.stringify(data));

            // Redirect based on role
            if (data.role === 'CUSTOMER') {
                window.location.href = 'customer-dashboard.html';
            } else if (data.role === 'ENGINEER') {
                if (data.isDefaultEngineer) {
                    window.location.href = 'admin-dashboard.html';
                } else {
                    window.location.href = 'engineer-dashboard.html';
                }
            }
        } else {
            alert('Login failed: ' + data.message);
        }
    } catch (error) {
        alert('Login error: ' + error.message);
    }
});

// Utility function for API calls with headers
function apiCall(url, method = 'GET', body = null) {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    if (!currentUser) {
        window.location.href = 'index.html';
        return;
    }

    const headers = {
        'Content-Type': 'application/json',
        'X-User-ID': currentUser.id,
        'X-User-Role': currentUser.role,
        'X-Username': currentUser.username
    };

    const config = {
        method,
        headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    return fetch(url, config);
}'''

        self.write_file(self.resources_base / "static/index.html", index_html)
        self.write_file(self.resources_base / "static/css/style.css", style_css)
        self.write_file(self.resources_base / "static/js/app.js", app_js)

    def generate_test_classes(self):
        """Generate basic test classes"""

        # Main test class
        main_test = '''package ticketmanagement.ticketservicemanagementv100;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TicketServiceManagementV100ApplicationTests {

    @Test
    void contextLoads() {
    }
}'''

        self.write_file(self.test_base / "TicketServiceManagementV100ApplicationTests.java", main_test)

    def generate_summary_report(self):
        """Generate comprehensive summary report"""
        report = f"""
🔥 COMPLETE SPRING BOOT PROJECT AUTO-GENERATION REPORT 🔥

📂 Project Path: {self.base_path}

✅ MAIN APPLICATION:
   - TicketServiceManagementV100Application.java

✅ ENUMS CREATED:
   - UserRole.java (CUSTOMER, ENGINEER)
   - TicketStatus.java (CREATED, ACKNOWLEDGED, IN_PROGRESS, CLOSED)

✅ ENTITIES CREATED/UPDATED:
   - User.java (Added companyName field)
   - Ticket.java (Changed tentativeResolutionDate to LocalDate)
   - Comment.java
   - Attachment.java
   - TicketCategory.java

✅ COMPLETE DTO PACKAGE:
   - LoginRequestDTO.java
   - LoginResponseDTO.java
   - UserRegistrationDTO.java
   - ProfileUpdateDTO.java
   - TicketCreationDTO.java
   - TicketUpdateDTO.java
   - TicketFilterDTO.java
   - CommentDTO.java
   - AttachmentDTO.java
   - CategoryDTO.java

✅ COMPLETE REPOSITORY PACKAGE:
   - UserRepository.java (with all query methods)
   - TicketRepository.java (with filtering queries)
   - CommentRepository.java
   - AttachmentRepository.java
   - TicketCategoryRepository.java

✅ COMPLETE SERVICE PACKAGE:
   - AuthService.java
   - UserService.java (complete CRUD + authentication)
   - TicketService.java (complete business logic)
   - CommentService.java
   - AttachmentService.java (file upload/download)
   - TicketCategoryService.java

✅ UTILITY CLASSES:
   - SecurityUtil.java (Header validation)
   - RoleValidator.java (Role-based access control)

✅ EXCEPTION HANDLING:
   - UnauthorizedException.java
   - UserNotFoundException.java
   - TicketNotFoundException.java
   - GlobalExceptionHandler.java

✅ COMPLETE CONTROLLER PACKAGE:
   - AuthController.java (Simple login/logout)
   - CustomerController.java (Customer-specific APIs)
   - EngineerController.java (Engineer-specific APIs)
   - AdminController.java (Admin engineer APIs)
   - PublicController.java (Public endpoints)

✅ CONFIGURATION:
   - CorsConfig.java (CORS configuration)

✅ MAVEN CONFIGURATION:
   - pom.xml (Complete with all dependencies)

✅ APPLICATION CONFIGURATION:
   - application.properties (Database, JPA, File upload, CORS)

✅ BASIC FRONTEND:
   - index.html (Login page)
   - style.css (Basic styling)
   - app.js (Header-based authentication logic)

✅ TEST STRUCTURE:
   - Main test class created
   - Test directories structured

🔄 AUTHENTICATION SYSTEM:
   - Type: Simple header-based authentication
   - Headers: X-User-ID, X-User-Role, X-Username
   - Storage: Frontend localStorage
   - Security: Role-based access control per endpoint

📋 API ENDPOINTS CREATED:

🔐 AUTH ENDPOINTS:
   - POST /api/auth/login
   - POST /api/auth/logout

👥 CUSTOMER ENDPOINTS:
   - POST /api/customer/tickets
   - GET /api/customer/tickets
   - POST /api/customer/tickets/{id}/comments
   - PUT /api/customer/profile

🔧 ENGINEER ENDPOINTS:
   - GET /api/engineer/tickets/unassigned
   - GET /api/engineer/tickets/assigned
   - PUT /api/engineer/tickets/{id}/acknowledge
   - PUT /api/engineer/tickets/{id}/update
   - DELETE /api/engineer/tickets/{id}
   - POST /api/engineer/customers

🛠 ADMIN ENDPOINTS:
   - POST /api/admin/engineers
   - GET /api/admin/users
   - DELETE /api/admin/users/{id}
   - POST /api/admin/categories
   - GET /api/admin/categories

🌐 PUBLIC ENDPOINTS:
   - GET /api/public/categories
   - GET /api/public/health

🚀 NEXT STEPS:
   1. Import project into IDE
   2. Configure MySQL database
   3. Run the application
   4. Test API endpoints
   5. Create admin user in database
   6. Develop frontend dashboards

📦 DEPENDENCIES INCLUDED:
   - Spring Boot Web
   - Spring Boot Data JPA
   - Spring Boot Validation
   - MySQL Connector
   - Lombok
   - Commons IO
   - Spring Boot DevTools
   - Spring Boot Test

🎉 PROJECT GENERATION COMPLETE!
Ready to run your complete Spring Boot Ticket Management System!
        """

        print(report)
        self.write_file(self.base_path / "PROJECT_GENERATION_REPORT.md", report)

    def run_complete_generation(self):
        """Run the complete project generation"""
        print("🚀 Starting Complete Spring Boot Project Generation...")

        # Create directory structure
        self.create_complete_directory_structure()

        # Generate main application
        self.generate_main_application_class()

        # Generate all packages
        self.generate_enums()
        self.generate_complete_entities()
        self.generate_complete_dtos()
        self.generate_complete_repositories()
        self.generate_complete_services()
        self.generate_utility_classes()
        self.generate_exception_classes()
        self.generate_complete_controllers()
        self.generate_config_classes()

        # Generate configuration files
        self.generate_maven_files()
        self.generate_application_properties()

        # Generate basic frontend
        self.generate_basic_frontend_files()

        # Generate tests
        self.generate_test_classes()

        # Generate summary
        self.generate_summary_report()

        print("\n🎉 COMPLETE PROJECT GENERATION SUCCESSFUL! 🎉")

# Usage
if __name__ == "__main__":
    # Your project path
    project_path = r"D:\Karan Ticket Project\TicketServiceManagementV100"

    # Create generator instance
    generator = CompleteSpringBootProjectGenerator(project_path)

    # Run complete generation
    generator.run_complete_generation()