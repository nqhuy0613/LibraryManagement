package my.project.librarymanagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import my.project.librarymanagement.enums.BorrowStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BorrowRecordResponse {
    private Long id;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private String note;

    private Long memberId;

    private Long bookId;
}
