package my.project.librarymanagement.service;

import my.project.librarymanagement.dto.request.BorrowBookRequest;
import my.project.librarymanagement.dto.request.ReturnBookRequest;
import my.project.librarymanagement.dto.response.BorrowRecordResponse;
import my.project.librarymanagement.entity.Book;
import my.project.librarymanagement.entity.BorrowRecord;
import my.project.librarymanagement.entity.User;
import my.project.librarymanagement.enums.BorrowStatus;
import my.project.librarymanagement.exception.BadRequestException;
import my.project.librarymanagement.exception.ResourceNotFoundException;
import my.project.librarymanagement.repository.BookRepository;
import my.project.librarymanagement.repository.BorrowRecordRepository;
import my.project.librarymanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowRecordService(BorrowRecordRepository borrowRecordRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public BorrowRecordResponse borrowBook(BorrowBookRequest borrowBookRequest) {
        //check user id + check cac exception user
        User user = findUserById(borrowBookRequest.getMemberId());
        validateUser(user);
        //check book id + check cac exception book
        Book book = findBookById(borrowBookRequest.getBookId());
        validateBook(book);
        //check due date
        LocalDate today = LocalDate.now();
        if(today.isAfter(borrowBookRequest.getDueDate())){
            throw new BadRequestException("Due date is must be after today");
        }
        //check exception user da muon 1 quyen sach nhu vay
        boolean check = this.borrowRecordRepository.existsByUser_IdAndBook_IdAndBorrowStatusIn(
                borrowBookRequest.getMemberId(), borrowBookRequest.getBookId(),
                List.of(BorrowStatus.BORROWED, BorrowStatus.OVERDUE)
        );

        if(check){
            throw new BadRequestException("This member is already borrowing this book and has not returned it yet");
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
        return toResponse(this.borrowRecordRepository.save(br));
    }

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
        br.setBorrowStatus(LocalDate.now().isAfter(br.getDueDate()) ? BorrowStatus.OVERDUE : BorrowStatus.RETURNED);
        if (returnBookRequest != null && returnBookRequest.getNote() != null && !returnBookRequest.getNote().isBlank()) {
            br.setNote(returnBookRequest.getNote().trim());
        }
        book.setAvailableCopies(book.getAvailableCopies()+1);
        this.bookRepository.save(book);
        //luu borrow record, chuyen sang response va return
        return toResponse(this.borrowRecordRepository.save(br));
    }

    public BorrowRecordResponse getById(Long id){
        BorrowRecord br = findBorrowRecordById(id);
        refreshOverdueStatusIfNeededInReadFlow(br);
        return toResponse(br);
    }

    public List<BorrowRecordResponse> getAllHistory() {
        List<BorrowRecord> brs = this.borrowRecordRepository.findAll();
        refreshOverdueStatuses(brs);
        return brs.stream()
                .map(x->toResponse(x))
                .toList();
    }

    public List<BorrowRecordResponse> getHistoryByMember(Long memberId) {
        User user = findUserById(memberId);
        List<BorrowRecord> brs = this.borrowRecordRepository.findAllByUser_Id(memberId);
        refreshOverdueStatuses(brs);
        return brs.stream().map(x->toResponse(x)).toList();
    }

    public List<BorrowRecordResponse> getOverdueRecords(){
        List<BorrowRecord> brs = this.borrowRecordRepository.findAll();
        refreshOverdueStatuses(brs);
        return brs.stream()
                .filter(x->x.getBorrowStatus().equals(BorrowStatus.OVERDUE))
                .map(x->toResponse(x)).toList();
    }

    private void refreshOverdueStatuses(List<BorrowRecord> records) {
        boolean changed = false;
        for (BorrowRecord record : records) {
            changed |= refreshOverdueStatus(record);
        }
        if (changed) {
            borrowRecordRepository.saveAll(records);
        }
    }

    private void refreshOverdueStatusIfNeededInReadFlow(BorrowRecord borrowRecord) {
        if (refreshOverdueStatus(borrowRecord)) {
            borrowRecordRepository.save(borrowRecord);
        }
    }

    private boolean refreshOverdueStatus(BorrowRecord borrowRecord) {
        if (borrowRecord.getBorrowStatus() == BorrowStatus.BORROWED
                && borrowRecord.getDueDate() != null
                && borrowRecord.getDueDate().isBefore(LocalDate.now())) {
            borrowRecord.setBorrowStatus(BorrowStatus.OVERDUE);
            return true;
        }
        return false;
    }



    private void validateUser(User user) {
        boolean userRole = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER"));
        if (!userRole) {
            throw new BadRequestException("The selected user does not have MEMBER role");
        }

        boolean userStatus = user.getStatus().equals("ACTIVE");
        if (!userStatus) {
            throw new BadRequestException("The selected user is not ACTIVE or is suspended");
        }
    }

    private User findUserById(Long memberId) {
        User user = userRepository.findById(memberId).orElseThrow(
                ()->new ResourceNotFoundException("User not found with id: " + memberId)
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
        if (book.getAvailableCopies() == 0)
            throw new BadRequestException("Book has no available copies");
    }

    private BorrowRecordResponse toResponse(BorrowRecord borrowRecord) {
        return BorrowRecordResponse.builder()
                .id(borrowRecord.getId())
                .borrowDate(borrowRecord.getBorrowDate())
                .dueDate(borrowRecord.getDueDate())
                .returnDate(borrowRecord.getReturnDate())
                .status(borrowRecord.getBorrowStatus())
                .note(borrowRecord.getNote())
                .memberId(borrowRecord.getUser().getId())
                .bookId(borrowRecord.getBook().getId())
                .build();
    }


}
