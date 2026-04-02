package my.project.libraryManagement.service;

import my.project.libraryManagement.dto.request.CreateUserRequest;
import my.project.libraryManagement.dto.request.UpdateUserRequest;
import my.project.libraryManagement.dto.response.UserResponse;
import my.project.libraryManagement.entity.User;
import my.project.libraryManagement.entity.Role;
import my.project.libraryManagement.enums.RoleName;
import my.project.libraryManagement.enums.UserStatus;
import my.project.libraryManagement.exception.DuplicateResourceException;
import my.project.libraryManagement.exception.ResourceNotFoundException;
import my.project.libraryManagement.repository.RoleRepository;
import my.project.libraryManagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findUser(id));
    }

    public UserResponse createUser(CreateUserRequest request) {
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        applyCreateUserData(user, request);

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUser(id);
        // check trùng cột unique create/update nên dùng existBy..IgnoreCaseAndIdNot để tối ưu
        String newEmail = request.getEmail().trim().toLowerCase();
        boolean emailDuplicated = userRepository.findAll().stream()
                .anyMatch(existing ->
                        existing.getEmail().equalsIgnoreCase(newEmail)
                                && !existing.getId().equals(id));

        if (emailDuplicated) {
            throw new DuplicateResourceException("Email already exists");
        }

        applyUpdateUserData(user, request);

        return toResponse(userRepository.save(user));
    }



    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames){
        Set<RoleName> effectiveRoles = (roleNames == null || roleNames.isEmpty()
                                                ? Set.of(RoleName.MEMBER)
                                                : roleNames);
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : effectiveRoles) {
            Role role = roleRepository.findByName(roleName).orElseThrow(
                    () -> new ResourceNotFoundException("Role not found")
            );
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }

    private void applyCreateUserData(User user, CreateUserRequest request) {
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        user.setStatus(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus());
        user.setRoles(resolveRoles(request.getRoles()));
    }

    private void applyUpdateUserData(User user, UpdateUserRequest request) {
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        user.setStatus(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus());
        user.setRoles(resolveRoles(request.getRoles()));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEnabled(user.isEnabled());
        response.setStatus(user.getStatus());
        response.setRoles(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()));
        return response;
    }
}
