package com.dinhngoctranduy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private boolean deleted;
    private boolean emailVerified = false;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<History> histories = new ArrayList<>();
}
