package my.project.librarymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BorrowBookRequest {
    @NotNull(message = "Member id is required")
    private Long memberId;

    @NotNull(message = "Book id is required")
    private Long bookId;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;
}
