package com.dinhngoctranduy.model.response;

import com.dinhngoctranduy.util.constant.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String fullName;
    private String password;
    private String address;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
}
