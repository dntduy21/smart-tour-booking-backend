package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.exceptions.InvalidDataException;
import com.dinhngoctranduy.model.*;
import com.dinhngoctranduy.model.dto.*;
import com.dinhngoctranduy.repository.*;
import com.dinhngoctranduy.service.BookingService;
import com.dinhngoctranduy.service.EmailService;
import com.dinhngoctranduy.service.HolidayService;
import com.dinhngoctranduy.service.VnPayService;
import com.dinhngoctranduy.util.constant.BookingStatus;
import com.dinhngoctranduy.util.constant.PaymentGateway;
import com.dinhngoctranduy.util.constant.PaymentStatus;
import com.dinhngoctranduy.util.constant.RefundStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final TourRepository tourRepo;
    private final PromotionRepository promoRepo;
    private final EmailService emailService;
    private final VnPayService vnPayService;
    private final UserRepository userRepository;
    private final HolidayService holidayService;

    public BookingResponse getById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new InvalidDataException("Booking not found with id = " + id));


        return mapToBookingResponse(booking);
    }

    public Object createBooking(BookingRequest req, String baseUrl) {
        Tour tour = tourRepo.findByIdAndDeletedFalse(req.getTourId()).orElseThrow(() -> {
            throw new InvalidDataException("Tour not found with id = " + req.getTourId());
        });
        Instant time = Instant.now();

        if (tour.getStartDate() != null) {
            Instant tourStartInstant = tour.getStartDate().atZone(ZoneId.systemDefault()).toInstant();
            if (time.isAfter(tourStartInstant) || time.equals(tourStartInstant)) {
                throw new RuntimeException("Đã quá hạn đặt tour. Không thể đặt tour sau hoặc trong ngày khởi hành.");
            }
        }
        int totalPeople = req.getAdults() + req.getChildren();
        if (tour.getCapacity() < totalPeople) throw new RuntimeException("Không đủ chỗ cho tour này.");
        else {
            tour.setCapacity(tour.getCapacity() - totalPeople);
        }
        double price = req.getAdults() * tour.getPriceAdults() + req.getChildren() * tour.getPriceChildren();

        Promotion promotion = null;
        if (req.getPromotionCode() != null) {
            Instant now = Instant.now();

            promotion = promoRepo.findByCodeAndActiveTrue(req.getPromotionCode())
                    .filter(p -> p.getUsageLimit() > 0 &&
                            !now.isBefore(p.getStartAt()) &&
                            !now.isAfter(p.getEndAt()))
                    .orElse(null);
            if (promotion != null) {

                price = price * (100 - promotion.getDiscountPercent()) / 100.0;
                promotion.setUsageLimit(promotion.getUsageLimit() - 1);
                promoRepo.save(promotion);
            } else {
                throw new InvalidDataException("Not found or expired promotion = " + req.getUserId());
            }
        }

        User user = null;
        if (req.getUserId() != null) {
            user = userRepository.findById(req.getUserId()).orElse(
                    null
            );
            if (user == null) {
                throw new InvalidDataException("Not found userId = " + req.getUserId());
            }
        }


        Booking booking = Booking.builder()
                .adults(req.getAdults())
                .children(req.getChildren())
                .bookingDate(Instant.now())
                .totalPrice(price)
                .status(BookingStatus.PENDING)
                .paymentStatus(Boolean.TRUE.equals(req.getIsCashPayment()) ? PaymentStatus.CASH : PaymentStatus.UNPAID)
                .guestName(req.getGuestName())
                .guestEmail(req.getGuestEmail())
                .guestPhone(req.getGuestPhone())
                .tour(tour)
                .promotion(promotion)
                .participants(req.getParticipants().stream().map(p -> {
                    try {
                        return new ObjectMapper().writeValueAsString(p);
                    } catch (JsonProcessingException e) {
                        return "";
                    }
                }).collect(Collectors.joining("##")))
                .build();

        if (user != null) {
            booking.setUser(user);
        }

        booking = bookingRepo.save(booking);

//        if (Boolean.TRUE.equals(req.getIsCashPayment())) {
//            try {
//                String subject = "Xác nhận đặt tour và hóa đơn của bạn";
//                String htmlContent = emailService.buildBookingConfirmationHtml(
//                        booking.getGuestName(),
//                        booking.getTour().getTitle() != null ? booking.getTour().getTitle() : booking.getTour().getTitle(),
//                        booking.getId().toString(),
//                        booking.getTotalPrice()
//                );
//                emailService.sendBookingConfirmationEmail(booking.getGuestEmail(), subject, htmlContent);
//            } catch (MessagingException e) {
//                System.err.println("Không thể gửi email xác nhận đặt chỗ cho booking " + booking.getId() + ": " + e.getMessage());
//            }
//
//            return mapToBookingResponse(booking);
//        }
//        return vnPayService.createPaymentUrl(booking, baseUrl);

        try {
            String subject = Boolean.TRUE.equals(req.getIsCashPayment())
                    ? "Xác nhận đặt tour và hóa đơn của bạn"
                    : "Đặt tour thành công - Vui lòng hoàn tất thanh toán chuyển khoản";
            String htmlContent = emailService.buildBookingConfirmationHtml(
                    booking.getGuestName(),
                    booking.getTour().getTitle(),
                    booking.getId().toString(),
                    booking.getTotalPrice()
            );
            emailService.sendBookingConfirmationEmail(booking.getGuestEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            System.err.println("Không thể gửi email xác nhận đặt chỗ cho booking " + booking.getId() + ": " + e.getMessage());
        }

        if (Boolean.TRUE.equals(req.getIsCashPayment())) {
            return mapToBookingResponse(booking);
        }
        return vnPayService.createPaymentUrl(booking, baseUrl);
    }

    public PaymentResponse handleVnPayReturn(Map<String, String> params) {
        String vnpTxnRef = params.get("vnp_TxnRef");
        Booking booking = bookingRepo.findById(Long.parseLong(vnpTxnRef)).orElseThrow();
        User user = booking.getUser();
        if ("00".equals(params.get("vnp_ResponseCode"))) {

            String responseCode = params.get("vnp_ResponseCode");
            String bankCode = params.get("vnp_BankCode");
            String errorMessage = getErrorMessage(responseCode); // optional
            String fullCallbackData = new Gson().toJson(params);
            booking.setPaymentStatus(PaymentStatus.PAID);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setInvoice(
                    Invoice.builder()
                            .amount(booking.getTotalPrice())
                            .issuedAt(Instant.now())
                            .booking(booking)
                            .build()
            );
            booking.setCheckout(
                    Checkout.builder()
                            .method(PaymentGateway.VNPAY)
                            .paidAt(Instant.now())
                            .amount(booking.getTotalPrice())
                            .status(PaymentStatus.PAID)
                            .transactionId(vnpTxnRef)
                            .responseCode(responseCode)
                            .errorMessage(errorMessage)
                            .bankCode(bankCode)
                            .callbackData(fullCallbackData)
                            .booking(booking)
                            .build()
            );


            bookingRepo.save(booking);

            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendBookingConfirmationEmail(
                            user == null ? booking.getGuestEmail() : user.getEmail(),
                            "Xác nhận đặt tour",
                            emailService.buildBookingConfirmationHtml(
                                    booking.getGuestName(),
                                    booking.getTour().getTitle(),
                                    booking.getId().toString(),
                                    booking.getTotalPrice()
                            )
                    );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            });

            return PaymentResponse.builder()
                    .paymentStatus(booking.getPaymentStatus().name()).build();
        } else {
            booking.setPaymentStatus(PaymentStatus.UNPAID);
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepo.save(booking);
            return PaymentResponse.builder()
                    .paymentStatus(booking.getPaymentStatus().name()).build();
        }
    }

    public CancelResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new InvalidDataException("Booking not found"));

        Tour tour = booking.getTour();
        Instant now = Instant.now();
        LocalDateTime startDate = tour.getStartDate();
        Instant startAt = startDate.atZone(ZoneId.systemDefault()).toInstant();

        if (now.isAfter(startAt) || now.equals(startAt)) {
            throw new InvalidDataException("Không thể hủy đặt tour vì tour đã bắt đầu.");
        }

        long hoursBefore = Duration.between(now, startAt).toHours();
        boolean isHoliday = holidayService.isHoliday(startAt);

        double total = booking.getTotalPrice();
        double penaltyPercent;

        if (isHoliday) {
            if (hoursBefore <= 24) {
                penaltyPercent = 100;
            } else if (hoursBefore <= 7 * 24) {
                penaltyPercent = 80;
            } else if (hoursBefore <= 15 * 24) {
                penaltyPercent = 50;
            } else if (hoursBefore <= 30 * 24) {
                penaltyPercent = 20;
            } else {
                penaltyPercent = 0;
            }
        } else {
            if (hoursBefore <= 24) {
                penaltyPercent = 90;
            } else if (hoursBefore <= 4 * 24) {
                penaltyPercent = 50;
            } else if (hoursBefore <= 7 * 24) {
                penaltyPercent = 30;
            } else if (hoursBefore <= 30 * 24) {
                penaltyPercent = 10;
            } else {
                penaltyPercent = 0;
            }
        }

        double refundAmount = total * (1 - penaltyPercent / 100.0);
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelDate(Instant.now());
        booking.setRefund(
                Refund.builder()
                        .refundAmount(refundAmount)
                        .refundPercent(penaltyPercent)
                        .status(RefundStatus.IN_PROCESS)
                        .createdAt(Instant.now())
                        .booking(booking)
                        .build()
        );

        booking.getTour().setCapacity(
                booking.getTour().getCapacity() + booking.getAdults() + booking.getChildren()
        );
        bookingRepo.save(booking);

        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendBookingCancelEmail(booking, booking.getTotalPrice(), penaltyPercent, refundAmount);
            } catch (Exception e) {
                System.err.println("Failed to send cancellation email for booking " + booking.getId() + ": " + e.getMessage());
            }
        });

        return new CancelResponse(booking.getId(), total, penaltyPercent, refundAmount);
    }

    public List<BookingResponse> searchBookings(Long bookingId, String keyword, Pageable pageable) {
        Specification<Booking> spec = Specification
                .where(BookingSpecification.hasBookingId(bookingId))
                .and(BookingSpecification.hasKeyword(keyword));

        return bookingRepo.findAll(spec, pageable)
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getUser() != null ? booking.getUser().getFullName() : booking.getGuestName())
                .customerEmail(booking.getUser() != null ? booking.getUser().getEmail() : booking.getGuestEmail())
                .customerPhone(booking.getUser() != null ? booking.getUser().getPhone() : booking.getGuestPhone())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .bookingAt(booking.getBookingDate())
                .promotionDto(PromotionDto.fromDomain(booking.getPromotion()))
                .participants(
                        booking.getParticipants() == null ? new ArrayList<>() :
                                Arrays.stream(
                                                booking.getParticipants().split("##")
                                        ).map(json -> {
                                            try {
                                                return new ObjectMapper().readValue(json, Participant.class);
                                            } catch (JsonProcessingException e) {
                                                return new Participant();
                                            }
                                        })
                                        .collect(Collectors.toList())
                )
                .cancelAt(booking.getCancelDate())
                .tour(
                        BookingResponse.TourDto.builder()
                                .tourId(booking.getTour().getId())
                                .title(booking.getTour().getTitle())
                                .endDate(booking.getTour().getEndDate())
                                .startDate(booking.getTour().getStartDate())
                                .build()
                )
                .build();

        if (booking.getRefund() != null) {
            response.setRefund(RefundDto.mapToDto(booking.getRefund()));
        }
        return response;
    }

    public void updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new InvalidDataException("Không tìm thấy đơn đặt tour với id = " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidDataException("Không thể cập nhật trạng thái đơn đã huỷ hoặc đã hoàn thành");
        }

        booking.setStatus(status);
        bookingRepo.save(booking);
    }

    public List<BookingResponse> getBookingsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidDataException("Không tìm thấy người dùng với id = " + userId));

        List<Booking> bookings = bookingRepo.findByUserId(userId);

        return bookings.stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }


    private String getErrorMessage(String code) {
        return switch (code) {
            case "00" -> "Giao dịch thành công";
            case "01" -> "Giao dịch thất bại do sai chữ ký";
            case "02" -> "Tài khoản không đủ tiền";
            // ...
            default -> "Lỗi không xác định";
        };
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    private static class PaymentResponse {
        private String paymentStatus;
    }

}
