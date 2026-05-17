package my.project.librarymanagement.service;

import my.project.librarymanagement.dto.request.borrowrecord.BorrowBookRequest;
import my.project.librarymanagement.dto.request.borrowrecord.ReturnBookRequest;
import my.project.librarymanagement.dto.response.BorrowRecordResponse;
import my.project.librarymanagement.entity.Book;
import my.project.librarymanagement.entity.BorrowRecord;
import my.project.librarymanagement.entity.User;
import my.project.librarymanagement.enums.BorrowStatus;
import my.project.librarymanagement.enums.RoleName;
import my.project.librarymanagement.enums.UserStatus;
import my.project.librarymanagement.exception.BadRequestException;
import my.project.librarymanagement.exception.ResourceNotFoundException;
import my.project.librarymanagement.repository.BookRepository;
import my.project.librarymanagement.repository.BorrowRecordRepository;
import my.project.librarymanagement.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public BorrowRecordService(BorrowRecordRepository borrowRecordRepository, BookRepository bookRepository, UserRepository userRepository, EmailService emailService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public BorrowRecordResponse borrowBook(BorrowBookRequest borrowBookRequest) {
        //check user id + check cac exception user
        User user = findUserById(borrowBookRequest.getUserId());
        validateUser(user);
        //check book id + check cac exception book
        Book book = findBookById(borrowBookRequest.getBookId());
        validateBook(book);
        //check due date
        LocalDate today = LocalDate.now();
        if(!borrowBookRequest.getDueDate().isAfter(today)){
            throw new BadRequestException("Due date must be after today");
        }
        //check exception user da muon 1 quyen sach nhu vay
        boolean check = this.borrowRecordRepository.existsByUser_IdAndBook_IdAndBorrowStatus(
                borrowBookRequest.getUserId(), borrowBookRequest.getBookId(),
                BorrowStatus.BORROWED
        );

        if(check){
            throw new BadRequestException("This user is already borrowing this book and has not returned it yet");
        }
        //giam avail copies, update status
        book.setAvailableCopies(book.getAvailableCopies()-1);
        this.bookRepository.save(book);
        //luu borrow record, chuyen sang response va return
        BorrowRecord br = new BorrowRecord();
        br.setUser(user);
        br.setBook(book);
        br.setDueDate(borrowBookRequest.getDueDate());
        br.setBorrowDate(today);
        br.setBorrowStatus(BorrowStatus.BORROWED);
        br.setNote(borrowBookRequest.getNote());

        BorrowRecord borrowRecord = this.borrowRecordRepository.save(br);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.sendBorrowSuccessEmail(borrowRecord);
            }
        });

        return toResponse(borrowRecord);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public BorrowRecordResponse returnBook(ReturnBookRequest returnBookRequest, Long BorrowRecordId) {
        // check borrow record id
        BorrowRecord br = findBorrowRecordById(BorrowRecordId);
        // check trang thai hien tai
        if(br.getBorrowStatus().equals(BorrowStatus.RETURNED)){
            throw new BadRequestException("This borrow record has already been returned");
        }
        // check avai+1 va total
        Book book = br.getBook();
        if(book.getAvailableCopies()+1>book.getTotalCopies()){
            throw new BadRequestException("Available copies cannot exceed total copies when returning a book");
        }
        // update borrow record return date va status, update book tang avai
        br.setReturnDate(LocalDate.now());
        br.setBorrowStatus(BorrowStatus.RETURNED);
        if (returnBookRequest != null && returnBookRequest.getNote() != null && !returnBookRequest.getNote().isBlank()) {
            br.setNote(returnBookRequest.getNote().trim());
        }
        book.setAvailableCopies(book.getAvailableCopies()+1);
        this.bookRepository.save(book);
        //luu borrow record, chuyen sang response va return

        BorrowRecord borrowRecord = this.borrowRecordRepository.save(br);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.sendReturnSuccessEmail(borrowRecord);
            }
        });


        return toResponse(borrowRecord);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public BorrowRecordResponse getById(Long id){
        BorrowRecord br = findBorrowRecordById(id);
        return toResponse(br);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<BorrowRecordResponse> getAllHistory() {
        List<BorrowRecord> brs = this.borrowRecordRepository.findAll();
        return brs.stream()
                .map(x->toResponse(x))
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN') or @borrowRecordSecurity.canViewUserHistory(#userId)")
    public List<BorrowRecordResponse> getHistoryUser(@P("userId") Long userId) {

        List<BorrowRecord> brs = this.borrowRecordRepository.findAllByUser_Id(userId);

        return brs.stream().map(x->toResponse(x)).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<BorrowRecordResponse> getOverdueRecords(){
        List<BorrowRecord> brs = this.borrowRecordRepository.findAll();

        return brs.stream()
                .filter(x->isOverdue(x))
                .map(x->toResponse(x)).toList();
    }


    private void validateUser(User user) {
        boolean userRole = user.getRoles().stream().anyMatch(role -> role.getName()==RoleName.MEMBER);
        if (!userRole) {
            throw new BadRequestException("The selected user does not have MEMBER role");
        }

        boolean userStatus = user.getStatus()== UserStatus.ACTIVE;
        if (!userStatus) {
            throw new BadRequestException("The selected user is not ACTIVE or is suspended");
        }
    }

    private User findUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User not found with id: " + userId)
        );
        return user;
    }

    private Book findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()->new ResourceNotFoundException("Book not found with id: " + bookId)
        );
        return book;
    }

    private BorrowRecord findBorrowRecordById(Long borrowRecordId) {
        BorrowRecord br = borrowRecordRepository.findById(borrowRecordId).orElseThrow(
                () -> new ResourceNotFoundException("Borrow record not found with id: " + borrowRecordId)
        );
        return br;
    }

    private void validateBook(Book book) {
        if (book.getAvailableCopies() <= 0)
            throw new BadRequestException("Book has no available copies");
    }

    private boolean isOverdue(BorrowRecord br) {
        if (br.getBorrowStatus()==BorrowStatus.BORROWED && br.getDueDate().isBefore(LocalDate.now())) {
            return true;

        }
        return false;
    }

    private BorrowRecordResponse toResponse(BorrowRecord borrowRecord) {
        return BorrowRecordResponse.builder()
                .id(borrowRecord.getId())
                .borrowDate(borrowRecord.getBorrowDate())
                .dueDate(borrowRecord.getDueDate())
                .returnDate(borrowRecord.getReturnDate())
                .status(borrowRecord.getBorrowStatus())
                .note(borrowRecord.getNote())
                .userId(borrowRecord.getUser().getId())
                .bookId(borrowRecord.getBook().getId())
                .overdue(isOverdue(borrowRecord))
                .build();
    }


}
