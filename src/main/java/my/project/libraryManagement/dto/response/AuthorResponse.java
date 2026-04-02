package my.project.libraryManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthorResponse {
    private Long id;
    private String name;
    private String biography;
}
