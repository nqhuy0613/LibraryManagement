package my.project.libraryManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.libraryManagement.enums.RoleName;
import my.project.libraryManagement.enums.UserStatus;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean enabled;
    private UserStatus status;
    private Set<RoleName> roles;
}
