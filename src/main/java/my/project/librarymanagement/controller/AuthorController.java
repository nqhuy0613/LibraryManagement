package my.project.librarymanagement.controller;

import jakarta.validation.Valid;
import my.project.librarymanagement.dto.request.CreateAuthorRequest;
import my.project.librarymanagement.dto.request.UpdateAuthorRequest;
import my.project.librarymanagement.dto.response.AuthorResponse;
import my.project.librarymanagement.dto.response.common.ApiResponse;
import my.project.librarymanagement.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAllAuthors() {return ResponseEntity.ok(success("Get all authors", authorService.getAllAuthors()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(@PathVariable("id") Long id){return ResponseEntity.ok(success("Get author by Id", authorService.getAuthorById(id)));}

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(@Valid @RequestBody CreateAuthorRequest createAuthorRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(success("Create an author", this.authorService.createAuthor(createAuthorRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(@Valid @RequestBody UpdateAuthorRequest updateAuthorRequest,  @PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(success("Update an author", this.authorService.updateAuthor(id, updateAuthorRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable("id") Long id){
        this.authorService.deleteAuthor(id);
        return ResponseEntity.ok(success("Delete an author",null));
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
