package my.project.librarymanagement.service;

import my.project.librarymanagement.dto.request.book.CreateBookRequest;
import my.project.librarymanagement.dto.request.book.UpdateBookRequest;
import my.project.librarymanagement.dto.response.BookResponse;
import my.project.librarymanagement.entity.Author;
import my.project.librarymanagement.entity.Book;
import my.project.librarymanagement.entity.Category;
import my.project.librarymanagement.exception.BadRequestException;
import my.project.librarymanagement.exception.DuplicateResourceException;
import my.project.librarymanagement.exception.ResourceNotFoundException;
import my.project.librarymanagement.repository.AuthorRepository;
import my.project.librarymanagement.repository.BookRepository;
import my.project.librarymanagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;

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
        book.setPublishedDate(createBookRequest.getPublishedDate());
        book.setTotalCopies(createBookRequest.getTotalCopies());
        book.setAvailableCopies(createBookRequest.getAvailableCopies());
        book.setShelfCode(createBookRequest.getShelfCode());
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
        book.setPublishedDate(update.getPublishedDate());
        book.setTotalCopies(update.getTotalCopies());
        book.setAvailableCopies(update.getAvailableCopies());
        book.setShelfCode(update.getShelfCode());
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
        bookResponse.setPublishedDate(book.getPublishedDate());
        bookResponse.setAvailableCopies(book.getAvailableCopies());
        bookResponse.setTotalCopies(book.getTotalCopies());
        bookResponse.setShelfCode(book.getShelfCode());
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
