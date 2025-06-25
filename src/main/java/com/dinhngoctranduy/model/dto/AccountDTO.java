package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.model.response.ResUserDTO;
import com.dinhngoctranduy.util.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private long id;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private LocalDate birthDate;
    private Gender gender;
    private ResUserDTO.RoleUser role;
    private boolean blocked;
    private boolean deleted;
    private String password;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
}
