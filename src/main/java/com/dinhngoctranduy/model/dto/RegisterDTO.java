package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.util.constant.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    private String phone;
    private String address;

    @Past
    private LocalDate birthDate;

    private Gender gender;
}
