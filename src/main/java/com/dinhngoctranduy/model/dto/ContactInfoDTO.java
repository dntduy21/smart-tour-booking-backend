package com.dinhngoctranduy.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactInfoDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String workingHours;
    private Boolean isPrimary;
}
