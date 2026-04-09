package my.project.librarymanagement.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.librarymanagement.enums.BookStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String description;
    private LocalDate publishedDate;
    private Long totalCopies;
    private Long availableCopies;
    private String shelfCode;
    private BookStatus status;
    private Long version;
    private String authorName;
    private String categoryName;
}
