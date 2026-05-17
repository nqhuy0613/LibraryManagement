package my.project.librarymanagement.service;

import my.project.librarymanagement.entity.Role;
import my.project.librarymanagement.entity.User;
import my.project.librarymanagement.enums.RoleName;
import my.project.librarymanagement.exception.ResourceNotFoundException;
import my.project.librarymanagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Current user not found");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public boolean hasRole(User user, RoleName roleName) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(name -> name == roleName);
    }

    public boolean isMember(User user) {
        return hasRole(user, RoleName.MEMBER);
    }

    public boolean isStaff(User user) {
        return hasRole(user, RoleName.ADMIN) || hasRole(user, RoleName.LIBRARIAN);
    }
}
