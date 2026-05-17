package my.project.librarymanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import my.project.librarymanagement.dto.response.common.ErrorResponse;
import my.project.librarymanagement.dto.response.common.FieldErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return ErrorResponse.buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        return ErrorResponse.buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI(), null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return ErrorResponse.buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorResponse> fieldErrorResponseList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x->toFieldErrorResponse(x))
                .toList();
        return ErrorResponse.buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST, request.getRequestURI(), fieldErrorResponseList);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request){
        return ErrorResponse.buildErrorResponse("Request violates databases constraints", HttpStatus.CONFLICT, request.getRequestURI(), null);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(
            ObjectOptimisticLockingFailureException ex, HttpServletRequest request
    ){
        return ErrorResponse.buildErrorResponse("The book was updated by another request. Please try again.", HttpStatus.CONFLICT, request.getRequestURI(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return ErrorResponse.buildErrorResponse(
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request
    ) {
        return ErrorResponse.buildErrorResponse(
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ErrorResponse> handleDisabledOrLockedAccount(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return ErrorResponse.buildErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler({
            AuthenticationException.class,
            AuthenticationCredentialsNotFoundException.class,
            AuthenticationServiceException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return ErrorResponse.buildErrorResponse(
                "Authentication failed",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return ErrorResponse.buildErrorResponse(
                "Access denied",
                HttpStatus.FORBIDDEN,
                request.getRequestURI(),
                null
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return ErrorResponse.buildErrorResponse("Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), null);
    }

    private FieldErrorResponse toFieldErrorResponse(FieldError fieldError) {
        return new FieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage());
    }


}
