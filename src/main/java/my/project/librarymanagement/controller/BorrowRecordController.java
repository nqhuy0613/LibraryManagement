package my.project.librarymanagement.controller;

import jakarta.validation.Valid;
import my.project.librarymanagement.dto.request.BorrowBookRequest;
import my.project.librarymanagement.dto.request.ReturnBookRequest;
import my.project.librarymanagement.dto.response.BorrowRecordResponse;
import my.project.librarymanagement.dto.response.common.ApiResponse;
import my.project.librarymanagement.service.BorrowRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrow-records")
public class BorrowRecordController {
    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> borrowBook(@Valid @RequestBody BorrowBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Borrow book successfully", borrowRecordService.borrowBook(request)));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> returnBook(@PathVariable Long id,
                                                                        @Valid @RequestBody(required = false) ReturnBookRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Return book successfully", borrowRecordService.returnBook(request,id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getAllHistory() {
        return ResponseEntity.ok(ApiResponse.success("Get all Borrow Records", borrowRecordService.getAllHistory()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Get Borrow Record By Id", borrowRecordService.getById(id)));
    }

    @GetMapping("/history/user/{userId}")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getHistoryUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Get Borrow Record By User Id", borrowRecordService.getHistoryUser(userId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getOverdueRecords() {
        return ResponseEntity.ok(ApiResponse.success("Get Overdue Borrow Record", borrowRecordService.getOverdueRecords()));
    }

    

}
