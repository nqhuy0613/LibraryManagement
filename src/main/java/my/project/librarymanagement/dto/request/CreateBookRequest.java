package my.project.librarymanagement.dto.request;



import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.librarymanagement.enums.BookStatus;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    @NotBlank(message = "isbn is required")
    @Size(max = 30, message = "isbn must be at most 30 characters")
    private String isbn;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title is must be at most 200 characters")
    private String title;

    private String description;
    private LocalDate publishedDate;

    @NotNull(message = "TotalCopies is required")
    @Positive(message = "Total copies must be greater than 0")
    private Long totalCopies;

    @NotNull(message = "AvailbleCopies is required")
    @PositiveOrZero(message = "Available copies must be zero or greater than 0")
    private Long availableCopies;

    @Size(max = 50, message = "shelfCode is must be at most 50 characters")
    private String shelfCode;


    private BookStatus status;

    @NotNull(message = "Author id is required")
    private Long authorId;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}
