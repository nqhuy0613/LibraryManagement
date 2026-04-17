package my.project.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.librarymanagement.enums.BorrowStatus;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private LocalDate borrowDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus borrowStatus = BorrowStatus.BORROWED;
    private LocalDate returnDate;
    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
