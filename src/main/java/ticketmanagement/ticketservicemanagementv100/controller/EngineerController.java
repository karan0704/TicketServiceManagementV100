package ticketmanagement.ticketservicemanagementv100.controller;

import org.springframework.http.HttpStatus;
import ticketmanagement.ticketservicemanagementv100.dto.EngineerRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.EngineerResponseDTO;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.service.EngineerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/engineers")
@RequiredArgsConstructor
public class EngineerController {

    private final EngineerService engineerService;

    @PostMapping
    public ResponseEntity<EngineerResponseDTO> createEngineer(@RequestBody EngineerRegistrationDTO dto,
                                                              @RequestHeader("X-User-Role") String role) {
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        EngineerResponseDTO saved = engineerService.createEngineer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<EngineerResponseDTO>> getAllEngineers() {
        return ResponseEntity.ok(engineerService.getAllEngineers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngineerResponseDTO> getEngineerById(@PathVariable Long id) {
        return engineerService.getEngineerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Engineer> updateEngineer(@PathVariable Long id, @RequestBody Engineer input) {
        try {
            Engineer updatedEngineer = engineerService.updateEngineer(id, input);
            return ResponseEntity.ok(updatedEngineer);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEngineer(@PathVariable Long id) {
        try {
            engineerService.deleteEngineer(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
