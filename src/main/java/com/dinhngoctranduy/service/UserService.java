package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.dto.Meta;
import com.dinhngoctranduy.model.dto.ResultPaginationDTO;
import com.dinhngoctranduy.model.response.ResCreateUserDTO;
import com.dinhngoctranduy.model.response.ResUpdateUserDTO;
import com.dinhngoctranduy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(true);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User không tồn tại");
        }
    }


    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Pageable pageable) {
        Page<User> page = this.userRepository.findAll(pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(page.getNumber());
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(page.getContent());
        return resultPaginationDTO;
    }

    public User handleUpdateUser(User reqUser) {
        User userCurrent = this.fetchUserById(reqUser.getId());
        if (userCurrent != null) {
            userCurrent.setFullName(reqUser.getFullName());
            userCurrent.setPassword(reqUser.getPassword());
            userCurrent.setAddress(reqUser.getAddress());
            userCurrent.setPhone(reqUser.getPhone());
            userCurrent.setBirthDate(reqUser.getBirthDate());
            userCurrent.setGender(reqUser.getGender());

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
        createUserDTO.setFullName(user.getFullName());
        createUserDTO.setEmail(user.getEmail());
        createUserDTO.setPhone(user.getPhone());
        createUserDTO.setAddress(user.getAddress());
        createUserDTO.setBirthDate(user.getBirthDate());
        createUserDTO.setGender(user.getGender());
        return createUserDTO;
    }

    public ResUpdateUserDTO resUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setFullName(user.getFullName());
        resUpdateUserDTO.setPassword(user.getPassword());
        resUpdateUserDTO.setPhone(user.getPhone());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setBirthDate(user.getBirthDate());
        resUpdateUserDTO.setGender(user.getGender());
        return resUpdateUserDTO;
    }

}
