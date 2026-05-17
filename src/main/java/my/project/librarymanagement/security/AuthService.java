package my.project.librarymanagement.security;

import my.project.librarymanagement.dto.request.auth.LoginRequest;
import my.project.librarymanagement.dto.request.auth.RegisterRequest;
import my.project.librarymanagement.dto.response.AuthResponse;
import my.project.librarymanagement.dto.response.UserResponse;
import my.project.librarymanagement.entity.Role;
import my.project.librarymanagement.entity.User;
import my.project.librarymanagement.enums.RoleName;
import my.project.librarymanagement.enums.UserStatus;
import my.project.librarymanagement.exception.DuplicateResourceException;
import my.project.librarymanagement.exception.ResourceNotFoundException;
import my.project.librarymanagement.repository.RoleRepository;
import my.project.librarymanagement.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().trim().toLowerCase();

        // check email
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists");
        }

        Role memberRole = roleRepository.findByName(RoleName.MEMBER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        // encode password, luu user vào db
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(email);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setEnabled(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(memberRole));

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim().toLowerCase();


        //check mat khau voi user do
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        loginRequest.getPassword()
                )
        );

        User user =  userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));


        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user){
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .user(toResponse(user))
                .build();
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        return toResponse(user);
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
