package ticketmanagement.ticketservicemanagementv100.service;

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
        // Create uploads directory if it doesn't exist
        File directory = new File("uploads/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("uploads/" + fileName);

        // Save file to disk
        Files.write(filePath, file.getBytes());

        // Create attachment entity
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
}