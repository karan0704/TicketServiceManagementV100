package ticketmanagement.ticketservicemanagementv100.dto;

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
}