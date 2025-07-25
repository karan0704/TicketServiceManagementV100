package ticketmanagement.ticketservicemanagementv100.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "engineers")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Engineer extends User {
    private String specialization;
    private boolean isDefaultEngineer = false;

    @OneToMany(mappedBy = "assignedEngineer")
    private List<Ticket> assignedTickets = new ArrayList<>();
}