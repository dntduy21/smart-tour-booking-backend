package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.domain.Specification;



public class BookingSpecification {
    public static Specification<Booking> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";

            // Join với user để truy cập user.fullName, user.email, user.phoneNumber
            Join<Booking, User> userJoin = root.join("user", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("guestName")), likePattern),
                    cb.like(cb.lower(root.get("guestEmail")), likePattern),
                    cb.like(cb.lower(root.get("guestPhone")), likePattern),

                    cb.like(cb.lower(userJoin.get("fullName")), likePattern),
                    cb.like(cb.lower(userJoin.get("email")), likePattern),
                    cb.like(cb.lower(userJoin.get("phone")), likePattern)
            );
        };
    }

    public static Specification<Booking> hasBookingId(Long bookingId) {
        return (root, query, cb) -> bookingId == null ? cb.conjunction() :
                cb.equal(root.get("id"), bookingId);
    }
}
