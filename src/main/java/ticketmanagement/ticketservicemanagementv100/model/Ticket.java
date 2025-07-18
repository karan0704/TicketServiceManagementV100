package ticketmanagement.ticketservicemanagementv100.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private LocalDateTime ticketCreateDate;
    private String customerCommentOnTicket;
    private String engineerCommentOnTicket;
    private LocalDate tentativeResolutionDate;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.CREATED;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer createdBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "engineer_id")
    private Engineer acknowledgedBy;

    @PrePersist
    public void prePersist() {
        this.ticketCreateDate = LocalDateTime.now();
    }

}