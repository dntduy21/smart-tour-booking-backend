package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.dto.ResultPaginationDTO;
import com.dinhngoctranduy.model.response.ResUpdateUserDTO;
import com.dinhngoctranduy.model.response.ResUserDTO;
import com.dinhngoctranduy.service.RoleService;
import com.dinhngoctranduy.service.UserService;
import com.dinhngoctranduy.util.error.IdInValidException;
import com.dinhngoctranduy.util.error.UserNotFoundExceptionCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder
            , RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws IdInValidException {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long id) throws UserNotFoundExceptionCustom {
        this.userService.handleBlockUser(id);
        return ResponseEntity.ok("Người dùng (ID: " + id + ") đã bị chặn thành công.");
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) throws UserNotFoundExceptionCustom {
        this.userService.handleUnblockUser(id);
        return ResponseEntity.ok("Người dùng (ID: " + id + ") đã được mở chặn thành công.");
    }

    @GetMapping("/usersbyemail/{keyword}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("keyword") String email) {
        User user = this.userService.fetchUserByEmail(email);
        return ResponseEntity.ok(this.userService.resUserDTO(user));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable long id) throws IdInValidException {
        User curUser = this.userService.fetchUserById(id);
        if (curUser == null) {
            throw new IdInValidException("Không tìm thấy người dùng có id = " + id);
        }
        return ResponseEntity.ok(this.userService.resUserDTO(curUser));
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userUpdate = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(userService.resUpdateUserDTO(userUpdate));
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<ResUserDTO>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<ResUserDTO> data = userService.searchUsers(keyword, pageable)
                .stream().map(userService::resUserDTO).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }
}
