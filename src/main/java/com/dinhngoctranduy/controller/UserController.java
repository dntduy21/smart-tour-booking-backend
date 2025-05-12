package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.service.UserService;
import com.dinhngoctranduy.util.error.IdInValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userNew = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userNew);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws IdInValidException {
        if (id > 1500)
            throw new IdInValidException("Id khong lon hon 1500");
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser());
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User userUpdate = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdate);
    }
}
