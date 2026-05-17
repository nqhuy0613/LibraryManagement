package my.project.librarymanagement.dto.request.borrowrecord;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnBookRequest {
    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;
}
