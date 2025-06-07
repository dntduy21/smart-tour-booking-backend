package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByBookingId(Long bookingId);
}
