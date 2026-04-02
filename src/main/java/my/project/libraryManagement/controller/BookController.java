package my.project.libraryManagement.controller;

import jakarta.validation.Valid;
import my.project.libraryManagement.dto.request.CreateBookRequest;
import my.project.libraryManagement.dto.request.UpdateBookRequest;
import my.project.libraryManagement.dto.response.BookResponse;
import my.project.libraryManagement.dto.response.common.ApiResponse;
import my.project.libraryManagement.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {return ResponseEntity.ok(success("Get all books", this.bookService.getAllBooks()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable("id") Long id){return ResponseEntity.ok(success("Get book by id", this.bookService.getBookById(id)));}

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody CreateBookRequest createBookRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(success("Create a book", this.bookService.createBook(createBookRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(@Valid @RequestBody UpdateBookRequest updateBookRequest, @PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(success("Update a book", this.bookService.updateBook(id, updateBookRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("id") Long id){
        this.bookService.deleteBook(id);
        return ResponseEntity.ok(success("Delete a book", null));
    }

    private<T> ApiResponse<T> success(String message, T data){
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setMessage(message);
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }
}
