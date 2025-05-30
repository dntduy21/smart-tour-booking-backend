package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "booking_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ tên hành khách không được để trống")
    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true) // Hoặc false nếu giới tính là bắt buộc cho mọi hành khách
    private Gender gender; // Giả sử bạn đã có Enum Gender

    @Email(message = "Email không hợp lệ")
    @Column(nullable = true) // Email có thể không bắt buộc với mọi hành khách (vd: trẻ em)
    private String email;

    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}