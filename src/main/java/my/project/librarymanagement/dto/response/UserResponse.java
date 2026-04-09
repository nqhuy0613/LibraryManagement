package my.project.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my.project.librarymanagement.enums.RoleName;
import my.project.librarymanagement.enums.UserStatus;

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
