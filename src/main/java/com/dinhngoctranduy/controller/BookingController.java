package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.*;
import com.dinhngoctranduy.model.request.BookingRequest;
import com.dinhngoctranduy.model.request.UpdateRefundStatusRequest;
import com.dinhngoctranduy.model.response.BookingResponse;
import com.dinhngoctranduy.model.response.CancelResponse;
import com.dinhngoctranduy.service.impl.BookingServiceImpl;
import com.dinhngoctranduy.service.impl.RefundService;
import com.dinhngoctranduy.util.SuccessPayload;
import com.dinhngoctranduy.util.constant.BookingStatus;
import com.dinhngoctranduy.util.constant.RefundStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping(value = "/{bookingId}/update-refund-status", consumes = "multipart/form-data")
    public ResponseEntity<RefundDTO> confirmRefundWithProof(
            @PathVariable Long bookingId,
            @RequestParam("status") String status,
            @RequestParam("proofImage") MultipartFile proofImage) {

        RefundStatus refundStatus = RefundStatus.valueOf(status.toUpperCase());
        RefundDTO updatedRefund = refundService.updateRefundStatusAndProof(bookingId, refundStatus, proofImage);
        return ResponseEntity.ok(updatedRefund);
    }

    @PostMapping("/{bookingId}/revert-cancellation")
    public ResponseEntity<BookingResponse> revertBookingCancellation(@PathVariable Long bookingId) {
        BookingResponse response = bookingService.revertBookingCancellation(bookingId);
        return ResponseEntity.ok(response);
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    private static class CreateResponse {
        private String vnPayUrl;
    }

}
