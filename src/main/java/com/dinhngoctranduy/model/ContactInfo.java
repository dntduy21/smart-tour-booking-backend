package com.dinhngoctranduy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "contact_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "address")
    private String address;

    @NotBlank
    @Column(name = "phone")
    private String phone;

    @Email
    @NotBlank
    @Column(name = "email")
    private String email;

    @Column(name = "working_hours")
    private String workingHours;

    // Xác định đâu là thông tin liên hệ chính sẽ hiển thị trên web
    @Column(name = "is_primary")
    private boolean isPrimary = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_by_user_id")
    private User managedBy;
}