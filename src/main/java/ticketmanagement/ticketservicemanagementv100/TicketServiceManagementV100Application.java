package ticketmanagement.ticketservicemanagementv100;

import ticketmanagement.ticketservicemanagementv100.model.Engineer;
import ticketmanagement.ticketservicemanagementv100.model.UserRole;
import ticketmanagement.ticketservicemanagementv100.repository.EngineerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TicketServiceManagementV100Application {

    public static void main(String[] args) {
        SpringApplication.run(TicketServiceManagementV100Application.class, args);
    }

    /**
     * CommandLineRunner to create a default engineer on application startup if one doesn't exist.
     * This provides an "inbuilt" engineer account for initial setup and testing.
     * Note: Password is not encoded in this simplified version.
     *
     * @param engineerRepository The repository for Engineer entities.
     * @return A CommandLineRunner bean.
     */
    @Bean
    public CommandLineRunner createDefaultEngineer(EngineerRepository engineerRepository) {
        return args -> {
            final String defaultEngineerUsername = "default_engineer";
            final String defaultEngineerPassword = "password";

            // Check if the default engineer already exists
            if (engineerRepository.findByUsername(defaultEngineerUsername).isEmpty()) {
                Engineer defaultEngineer = new Engineer();
                defaultEngineer.setUsername(defaultEngineerUsername);
                defaultEngineer.setPassword(defaultEngineerPassword); // Password is now plain text
                defaultEngineer.setRole(UserRole.ENGINEER);
                engineerRepository.save(defaultEngineer);
                System.out.println("Default engineer '" + defaultEngineerUsername + "' created successfully!");
            } else {
                System.out.println("Default engineer '" + defaultEngineerUsername + "' already exists.");
            }
        };
    }
}
