package ticketmanagement.ticketservicemanagementv100.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "engineers")
public class Engineer extends User {
    public Engineer(Long id, String username, String password) {
        super(id, username, password);
    }

}

