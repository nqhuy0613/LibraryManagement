package my.project.libraryManagement.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
}
