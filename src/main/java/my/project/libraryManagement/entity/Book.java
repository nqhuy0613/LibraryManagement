package my.project.libraryManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.libraryManagement.enums.BookStatus;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;
    private String title;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant publishDate;
    private Long totalCopies;
    private Long availableCopies;
    private String shelfCode;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private Long version;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BorrowRecord> borrowRecords;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
