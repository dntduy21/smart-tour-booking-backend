package com.dinhngoctranduy.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String username;
    private String email;
    private String address;
}