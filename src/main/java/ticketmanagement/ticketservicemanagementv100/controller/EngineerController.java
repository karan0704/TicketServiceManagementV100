package ticketmanagement.ticketservicemanagementv100.controller;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Engineer> createEngineer(@RequestBody Engineer engineer, @RequestHeader("X-User-Role") String role) {
        if (!"ENGINEER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        Engineer saved = engineerService.createEngineer(engineer);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Engineer>> getAllEngineers() {
        return ResponseEntity.ok(engineerService.getAllEngineers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Engineer> getEngineerById(@PathVariable Long id) {
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
