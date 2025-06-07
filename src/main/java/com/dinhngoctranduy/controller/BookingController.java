package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.*;
import com.dinhngoctranduy.service.impl.BookingServiceImpl;
import com.dinhngoctranduy.service.impl.RefundService;
import com.dinhngoctranduy.util.SuccessPayload;
import com.dinhngoctranduy.util.constant.BookingStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;
    private final RefundService refundService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, HttpServletRequest servletRequest) {
        String baseUrl = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI(), "");
        Object response = bookingService.createBooking(request, baseUrl);
        return response instanceof String ? ResponseEntity.ok(
                CreateResponse.builder()
                                .vnPayUrl(
                                        (String) response
                                ).build()
        ) : ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        CancelResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> handleVnPayReturn(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(
                bookingService.handleVnPayReturn(params)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @GetMapping("/search")
    public List<BookingResponse> getAllOrSearchBookings(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingService.searchBookings(bookingId, keyword, pageable);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status
    ) {
        bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(SuccessPayload.build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUser(@PathVariable Long userId) {
        List<BookingResponse> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{bookingId}/update-refund-status")
    public ResponseEntity<?> updateRefundStatusByBooking(@PathVariable Long bookingId,
                                                 @RequestBody UpdateRefundStatusRequest request) {
        RefundDto updatedRefund = refundService.updateRefundStatusByBookingId(bookingId, request.getStatus());
        return ResponseEntity.ok(updatedRefund);
    }



    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    private static class CreateResponse {
        private String vnPayUrl;
    }

}
