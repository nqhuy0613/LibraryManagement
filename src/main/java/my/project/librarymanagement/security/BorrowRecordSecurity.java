package my.project.librarymanagement.security;

import my.project.librarymanagement.entity.User;
import my.project.librarymanagement.service.CurrentUserService;
import org.springframework.stereotype.Component;

@Component("borrowRecordSecurity")
public class BorrowRecordSecurity {
    private final CurrentUserService currentUserService;

    public BorrowRecordSecurity(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public boolean canViewUserHistory(Long userId) {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUserService.isStaff(currentUser)) {
            return true;
        }

        return currentUserService.isMember(currentUser) && currentUser.getId().equals(userId);
    }
}
