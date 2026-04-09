package my.project.librarymanagement.controller;

import jakarta.validation.Valid;
import my.project.librarymanagement.dto.request.CreateUserRequest;
import my.project.librarymanagement.dto.request.UpdateUserRequest;
import my.project.librarymanagement.dto.response.UserResponse;
import my.project.librarymanagement.dto.response.common.ApiResponse;
import my.project.librarymanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {return ResponseEntity.ok(success("Get all users", this.userService.getAllUsers()));}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") Long id){return ResponseEntity.ok(success("Get user by id", this.userService.getUserById(id)));}

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(success("Create an user", userService.createUser(createUserRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest, @PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(success("Update an user", userService.updateUser(id, updateUserRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id){
        this.userService.deleteUser(id);
        return ResponseEntity.ok(success("Delete an user",null));
    }

    private<T> ApiResponse<T> success(String message, T data){
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setMessage(message);
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }
}
