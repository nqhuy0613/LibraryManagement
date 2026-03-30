package my.project.libraryManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.libraryManagement.enums.BorrowStatus;

import java.time.Instant;

@Entity
@Table(name = "borrowrecords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Instant borrowDate;
    @Enumerated(EnumType.STRING)
    private BorrowStatus borrowStatus;
    private Instant returnDate;
    private Instant dueDate;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
