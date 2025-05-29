package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.ContactStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người dùng đăng nhập (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Thông tin liên hệ
    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String nationality; // Quốc tịch

    @NotBlank
    private String country; // Quốc gia

    @NotBlank
    private String phone;

    // Thông tin yêu cầu tour
    private boolean isDomestic;      // Trong nước
    private boolean isInternational; // Nước ngoài

    @Column(length = 1000)
    private String destinationDetail;

    private Instant startDate;
    private Instant endDate;

    private boolean isDateFixed; // ngày cố định hay linh hoạt

    private String travelPreference; // ví dụ: "Máy bay", "Tàu hỏa"

    private int numberOfAdults;
    private int numberOfChildren;

    private int numberOfRooms;
    private String hotelStandard; // chuẩn khách sạng

    // Yêu cầu phòng
    private boolean smoking;
    private boolean nonSmoking;
    private boolean singleRoom;
    private boolean doubleRoom;
    private boolean kingQueenBed;
    private boolean vipRoom;

    @Enumerated(EnumType.STRING)
    private ContactStatus contactStatus;

}
