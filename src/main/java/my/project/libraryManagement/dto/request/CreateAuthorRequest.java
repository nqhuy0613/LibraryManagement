package my.project.libraryManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateAuthorRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 120, message = "Author name must be at most 120 characters")
    private String name;

    @Size(max = 2000, message = "Biography must be at most 2000 characters")
    private String biography;
}
