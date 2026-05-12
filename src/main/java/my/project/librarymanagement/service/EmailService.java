package my.project.librarymanagement.service;

import my.project.librarymanagement.entity.BorrowRecord;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBorrowSuccessEmail(BorrowRecord borrowRecord) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(borrowRecord.getUser().getEmail());
        message.setSubject("Borrow book successfully");
        message.setText("""
                Hello %s,

                You have borrowed a book successfully.

                Book: %s
                Borrow date: %s
                Due date: %s

                Please return the book on time.
                """.formatted(
                borrowRecord.getUser().getFullName(),
                borrowRecord.getBook().getTitle(),
                borrowRecord.getBorrowDate(),
                borrowRecord.getDueDate()
        ));

        mailSender.send(message);
    }

    public void sendReturnSuccessEmail(BorrowRecord borrowRecord) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(borrowRecord.getUser().getEmail());
        message.setSubject("Return book successfully");
        message.setText("""
                Hello %s,

                You have returned a book successfully.

                Book: %s
                Borrow date: %s
                Return date: %s

                Thank you.
                """.formatted(
                borrowRecord.getUser().getFullName(),
                borrowRecord.getBook().getTitle(),
                borrowRecord.getBorrowDate(),
                borrowRecord.getReturnDate()
        ));

        mailSender.send(message);
    }
}

