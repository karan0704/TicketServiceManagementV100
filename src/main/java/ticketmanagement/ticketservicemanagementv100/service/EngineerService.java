package ticketmanagement.ticketservicemanagementv100.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.dto.EngineerRegistrationDTO;
import ticketmanagement.ticketservicemanagementv100.dto.EngineerResponseDTO;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.model.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EngineerService {

    private final EngineerRepository engineerRepository;

    public EngineerResponseDTO createEngineer(EngineerRegistrationDTO dto) {
        Engineer engineer = new Engineer();
        engineer.setUsername(dto.getUsername());
        engineer.setPassword(dto.getPassword()); // TODO: hash password later
        engineer.setRole(UserRole.ENGINEER);
        Engineer saved = engineerRepository.save(engineer);
        return new EngineerResponseDTO(saved.getId(), saved.getUsername());
    }

    public List<EngineerResponseDTO> getAllEngineers() {
        return engineerRepository.findAll()
                .stream()
                .map(e -> new EngineerResponseDTO(e.getId(), e.getUsername()))
                .collect(Collectors.toList());
    }

    public Optional<EngineerResponseDTO> getEngineerById(Long id) {
        return engineerRepository.findById(id)
                .map(e -> new EngineerResponseDTO(e.getId(), e.getUsername()));
    }

    public Engineer updateEngineer(Long id, Engineer input) {
        return engineerRepository.findById(id)
                .map(engineer -> {
                    engineer.setUsername(input.getUsername());
                    engineer.setPassword(input.getPassword()); // TODO: hash password later
                    return engineerRepository.save(engineer);
                })
                .orElseThrow(() -> new EntityNotFoundException("Engineer not found with id " + id));
    }

    public void deleteEngineer(Long id) {
        if (!engineerRepository.existsById(id)) {
            throw new EntityNotFoundException("Engineer not found with id " + id);
        }
        engineerRepository.deleteById(id);
    }
}
