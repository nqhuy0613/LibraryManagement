package my.project.librarymanagement.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private int status;
    private String path;
    private List<FieldErrorResponse> fieldErrors;
    private LocalDateTime timestamp;

    public static ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, String path, List<FieldErrorResponse> fieldError) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .message(message)
                .status(status.value())
                .path(path)
                .fieldErrors(fieldError)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}
