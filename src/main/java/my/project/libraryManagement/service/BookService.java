package my.project.libraryManagement.service;

import my.project.libraryManagement.dto.request.CreateBookRequest;
import my.project.libraryManagement.dto.request.UpdateBookRequest;
import my.project.libraryManagement.dto.response.BookResponse;
import my.project.libraryManagement.entity.Author;
import my.project.libraryManagement.entity.Book;
import my.project.libraryManagement.entity.Category;
import my.project.libraryManagement.enums.BookStatus;
import my.project.libraryManagement.exception.BadRequestException;
import my.project.libraryManagement.exception.DuplicateResourceException;
import my.project.libraryManagement.exception.ResourceNotFoundException;
import my.project.libraryManagement.repository.AuthorRepository;
import my.project.libraryManagement.repository.BookRepository;
import my.project.libraryManagement.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository) {

        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<BookResponse> getAllBooks() {
        return this.bookRepository.findAll().stream()
                .map(x->this.toResponse(x))
                .toList();
    }

    public BookResponse getBookById(Long id) {
        return this.toResponse(findBook(id));
    }

    public BookResponse createBook(CreateBookRequest createBookRequest) {
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        if(bookRepository.existsByIsbn(createBookRequest.getIsbn().trim())) {
            throw new DuplicateResourceException("Book already exists");
        }
        validateCopies(createBookRequest.getTotalCopies(),createBookRequest.getAvailableCopies());
        Book book = new Book();
        book.setIsbn(createBookRequest.getIsbn().trim());
        book.setTitle(createBookRequest.getTitle());
        book.setDescription(createBookRequest.getDescription());
        book.setPublishDate(createBookRequest.getPublishedDate());
        book.setTotalCopies(createBookRequest.getTotalCopies());
        book.setAvailableCopies(createBookRequest.getAvailableCopies());
        book.setShelfCode(createBookRequest.getShelfCode());
        book.setStatus(createBookRequest.getStatus()!= null ? createBookRequest.getStatus(): BookStatus.AVAILABLE);
        book.setAuthor(findAuthor(createBookRequest.getAuthorId()));
        book.setCategory(findCategory(createBookRequest.getCategoryId()));
        return toResponse(this.bookRepository.save(book));
    }

    public BookResponse updateBook(Long id, UpdateBookRequest update){
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        Book book = findBook(id);
        String newIsbn  = update.getIsbn().trim();
        boolean isDuplicate = bookRepository.findAll().stream()
                .anyMatch(x -> x.getIsbn().equals(newIsbn)
                && !x.getId().equals(id));
        if(isDuplicate) {
            throw new DuplicateResourceException("Book already exists");
        }
        validateCopies(update.getTotalCopies(),update.getAvailableCopies());
        book.setIsbn(update.getIsbn().trim());
        book.setTitle(update.getTitle());
        book.setDescription(update.getDescription());
        book.setPublishDate(update.getPublishedDate());
        book.setTotalCopies(update.getTotalCopies());
        book.setAvailableCopies(update.getAvailableCopies());
        book.setShelfCode(update.getShelfCode());
        if(update.getStatus() != null) {
            book.setStatus(update.getStatus());
        }
        book.setAuthor(findAuthor(update.getAuthorId()));
        book.setCategory(findCategory(update.getCategoryId()));
        return toResponse(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        Book book = findBook(id);
        bookRepository.delete(book);
    }

    private BookResponse toResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setDescription(book.getDescription());
        bookResponse.setPublishedDate(book.getPublishDate());
        bookResponse.setAvailableCopies(book.getAvailableCopies());
        bookResponse.setTotalCopies(book.getTotalCopies());
        bookResponse.setShelfCode(book.getShelfCode());
        bookResponse.setStatus(book.getStatus());
        bookResponse.setVersion(book.getVersion());
        bookResponse.setAuthorName(book.getAuthor().getName());
        bookResponse.setCategoryName(book.getCategory().getName());
        return bookResponse;

    }

    private void validateCopies(Long totalCopies, Long availableCopies) {
        if (availableCopies > totalCopies) {
            throw new BadRequestException("Available copies cannot be greater than total copies");
        }
    }

    private Book findBook(Long id) {
        return this.bookRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Book not found")
        );
    }

    private Author findAuthor(Long id) {
        return this.authorRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Author not found")
        );
    }

    private Category findCategory(Long id) {
        return this.categoryRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Category not found")
        );

    }
}
