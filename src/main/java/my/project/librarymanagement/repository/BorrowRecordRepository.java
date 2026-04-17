package my.project.librarymanagement.repository;

import my.project.librarymanagement.entity.BorrowRecord;
import my.project.librarymanagement.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long> {
    boolean existsByUser_IdAndBook_IdAndBorrowStatusIn(
            Long userId,
            Long bookId,
            Collection<BorrowStatus> statuses
    );
    List<BorrowRecord> findAllByUser_Id(Long memberId);
}
