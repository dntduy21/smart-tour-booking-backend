package com.dinhngoctranduy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    private String name;
    private String phone;
    private String gender; // male, female
}
