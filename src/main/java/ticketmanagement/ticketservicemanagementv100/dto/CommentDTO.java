package ticketmanagement.ticketservicemanagementv100.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private String content;
    private Long ticketId;
    private Long authorId;
}
