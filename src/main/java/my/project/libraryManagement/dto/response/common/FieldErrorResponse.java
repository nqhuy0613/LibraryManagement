package my.project.libraryManagement.dto.response.common;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FieldErrorResponse {
    private String field;
    private String message;
}

