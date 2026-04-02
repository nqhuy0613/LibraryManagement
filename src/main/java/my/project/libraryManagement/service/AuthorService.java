package my.project.libraryManagement.service;

import my.project.libraryManagement.dto.request.CreateAuthorRequest;
import my.project.libraryManagement.dto.request.UpdateAuthorRequest;
import my.project.libraryManagement.dto.response.AuthorResponse;
import my.project.libraryManagement.entity.Author;
import my.project.libraryManagement.exception.ResourceNotFoundException;
import my.project.libraryManagement.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(x->this.toResponse(x))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id){
        Author author = findAuthor(id);
        return this.toResponse(author);
    }

    public AuthorResponse createAuthor(CreateAuthorRequest createAuthorRequest){
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        Author author = new Author();
        author.setName(createAuthorRequest.getName().trim());
        author.setBiography(createAuthorRequest.getBiography());
        authorRepository.save(author);
        return this.toResponse(author);
    }

    public AuthorResponse updateAuthor(Long id, UpdateAuthorRequest updateAuthorRequest){
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        Author author = findAuthor(id);
        author.setName(updateAuthorRequest.getName().trim());
        author.setBiography(updateAuthorRequest.getBiography());
        authorRepository.save(author);
        return this.toResponse(author);
    }

    public void deleteAuthor(Long id){
        Author author = findAuthor(id);
        authorRepository.delete(author);
    }

    private AuthorResponse toResponse(Author author){
        return new AuthorResponse(author.getId(), author.getName(), author.getBiography());
    }


    private Author findAuthor(Long id){
        return this.authorRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("author not found"));

    }
}
