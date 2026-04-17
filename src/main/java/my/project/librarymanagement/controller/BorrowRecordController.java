package my.project.librarymanagement.controller;

import jakarta.validation.Valid;
import my.project.librarymanagement.dto.request.BorrowBookRequest;
import my.project.librarymanagement.dto.request.ReturnBookRequest;
import my.project.librarymanagement.dto.response.BorrowRecordResponse;
import my.project.librarymanagement.dto.response.common.ApiResponse;
import my.project.librarymanagement.repository.BookRepository;
import my.project.librarymanagement.repository.BorrowRecordRepository;
import my.project.librarymanagement.repository.UserRepository;
import my.project.librarymanagement.service.BorrowRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
                .body(success("Book borrowed successfully", borrowRecordService.borrowBook(request)));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> returnBook(@PathVariable Long id,
                                                                        @Valid @RequestBody(required = false) ReturnBookRequest request) {
        return ResponseEntity.ok(success("Book returned successfully", borrowRecordService.returnBook(request,id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getAllHistory() {
        return ResponseEntity.ok(success("Borrow history fetched successfully", borrowRecordService.getAllHistory()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(success("Borrow record fetched successfully", borrowRecordService.getById(id)));
    }

    @GetMapping("/history/member/{memberId}")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getHistoryByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(success("Member borrow history fetched successfully", borrowRecordService.getHistoryByMember(memberId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getOverdueRecords() {
        return ResponseEntity.ok(success("Overdue borrow records fetched successfully", borrowRecordService.getOverdueRecords()));
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
