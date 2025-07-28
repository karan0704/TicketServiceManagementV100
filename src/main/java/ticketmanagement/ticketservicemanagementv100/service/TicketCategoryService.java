package ticketmanagement.ticketservicemanagementv100.service;

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
}