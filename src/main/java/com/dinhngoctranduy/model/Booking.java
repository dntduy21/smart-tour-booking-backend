package com.dinhngoctranduy.model;

import com.dinhngoctranduy.util.constant.BookingStatus;
import com.dinhngoctranduy.util.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant bookingDate;
    private Instant cancelDate;

    private int adults;
    private int children;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Guest booking info
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Checkout checkout;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Refund refund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    public String getEmail() {
        return user == null ? guestEmail : user.getEmail();
    }

    public String getUserName() {
        return user == null ? guestName : user.getFullName();
    }

    public void setParticipantsAndLink(List<Participant> participants) {
        // Xóa danh sách cũ nếu có
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        } else {
            this.participants.clear();
        }

        // Thêm danh sách mới và thiết lập liên kết ngược lại
        if (participants != null) {
            for (Participant p : participants) {
                p.setBooking(this); // Quan trọng: Gán booking này cho từng participant
                this.participants.add(p);
            }
        }
    }
}