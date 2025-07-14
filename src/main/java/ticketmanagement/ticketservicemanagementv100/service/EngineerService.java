package ticketmanagement.ticketservicemanagementv100.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.model.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EngineerService {

    private final EngineerRepository engineerRepository;

    /**
     * Creates a new Engineer.
     *
     * @param engineer The Engineer object to be saved.
     * @return The saved Engineer object.
     */
    public Engineer createEngineer(Engineer engineer) {
        engineer.setRole(UserRole.ENGINEER);
        return engineerRepository.save(engineer);
    }

    /**
     * Retrieves all Engineers.
     *
     * @return A list of all Engineer objects.
     */
    public List<Engineer> getAllEngineers() {
        return engineerRepository.findAll();
    }

    /**
     * Retrieves an Engineer by their ID.
     *
     * @param id The ID of the engineer to retrieve.
     * @return An Optional containing the Engineer if found, or empty if not.
     */
    public Optional<Engineer> getEngineerById(Long id) {
        return engineerRepository.findById(id);
    }

    /**
     * Updates an existing Engineer.
     *
     * @param id The ID of the engineer to update.
     * @param engineerDetails The Engineer object containing updated details.
     * @return The updated Engineer object.
     * @throws EntityNotFoundException if no engineer with the given ID is found.
     */
    public Engineer updateEngineer(Long id, Engineer engineerDetails) {
        return engineerRepository.findById(id)
                .map(engineer -> {
                    engineer.setUsername(engineerDetails.getUsername());
                    engineer.setPassword(engineerDetails.getPassword());
                    return engineerRepository.save(engineer);
                })
                .orElseThrow(() -> new EntityNotFoundException("Engineer not found with id " + id));
    }

    /**
     * Deletes an Engineer by their ID.
     *
     * @param id The ID of the engineer to delete.
     * @throws EntityNotFoundException if no engineer with the given ID is found.
     */
    public void deleteEngineer(Long id) {
        if (!engineerRepository.existsById(id)) {
            throw new EntityNotFoundException("Engineer not found with id " + id);
        }
        engineerRepository.deleteById(id);
    }
}