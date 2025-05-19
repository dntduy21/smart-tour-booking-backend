package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.response.ResCreateUserDTO;
import com.dinhngoctranduy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public User handleUpdateUser(User reqUser) {
        User userCurrent = this.fetchUserById(reqUser.getId());
        if (userCurrent != null) {
            userCurrent.setUsername(reqUser.getUsername());
            userCurrent.setPassword(reqUser.getPassword());
            userCurrent.setEmail(reqUser.getEmail());
            userCurrent.setPhone(reqUser.getPhone());
            userCurrent.setAddress(reqUser.getAddress());

            userCurrent = this.userRepository.save(userCurrent);
        }
        return userCurrent;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public boolean isUsernameExists(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public boolean isEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO resCreateUserDTO(User user) {
        ResCreateUserDTO createUserDTO = new ResCreateUserDTO();
        createUserDTO.setId(user.getId());
        createUserDTO.setUsername(user.getUsername());
        createUserDTO.setEmail(user.getEmail());
        createUserDTO.setAddress(user.getAddress());
        return createUserDTO;
    }
}
