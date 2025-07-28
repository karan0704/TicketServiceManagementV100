package ticketmanagement.ticketservicemanagementv100.dto;

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
}