package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.util.error.InvalidDataException;
import com.dinhngoctranduy.model.*;
import com.dinhngoctranduy.model.dto.*;
import com.dinhngoctranduy.model.request.BookingRequest;
import com.dinhngoctranduy.model.response.BookingResponse;
import com.dinhngoctranduy.model.response.CancelResponse;
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
import jakarta.transaction.Transactional;
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

    @Transactional
    public Object createBooking(BookingRequest req, String baseUrl) {

        Tour tour = tourRepo.findByIdAndDeletedFalse(req.getTourId())
                .orElseThrow(() -> new InvalidDataException("Không tìm thấy tour với id = " + req.getTourId()));

        // Kiểm tra ngày khởi hành của tour
        if (tour.getStartDate() != null && Instant.now().isAfter(tour.getStartDate().atZone(ZoneId.systemDefault()).toInstant())) {
            throw new InvalidDataException("Đã quá hạn đặt tour. Không thể đặt tour đã hoặc sắp khởi hành.");
        }

        // Kiểm tra sức chứa của tour
        int totalPeople = req.getAdults() + req.getChildren();
        if (tour.getCapacity() < totalPeople) {
            throw new InvalidDataException("Không đủ chỗ cho tour này. Chỉ còn lại " + tour.getCapacity() + " chỗ.");
        }

        User user = null;
        if (req.getUserId() != null) {
            user = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new InvalidDataException("Không tìm thấy người dùng với id = " + req.getUserId()));
        }

        // Tính giá gốc
        double price = req.getAdults() * tour.getPriceAdults() + req.getChildren() * tour.getPriceChildren();

        // Áp dụng khuyến mãi (nếu có)
        Promotion promotion = null;
        if (req.getPromotionCode() != null && !req.getPromotionCode().trim().isEmpty()) {
            Instant now = Instant.now();
            promotion = promoRepo.findByCodeAndActiveTrue(req.getPromotionCode())
                    .orElseThrow(() -> new InvalidDataException("Mã khuyến mãi không hợp lệ hoặc đã hết hạn."));

            if (promotion.getUsageLimit() <= 0)
                throw new InvalidDataException("Mã khuyến mãi này đã hết lượt sử dụng.");
            if (now.isBefore(promotion.getStartAt()))
                throw new InvalidDataException("Chưa đến ngày áp dụng mã khuyến mãi này.");
            if (now.isAfter(promotion.getEndAt())) throw new InvalidDataException("Mã khuyến mãi đã hết hạn sử dụng.");

            // Áp dụng giảm giá và cập nhật lại lượt sử dụng
            price *= (1 - promotion.getDiscountPercent() / 100.0);
            promotion.setUsageLimit(promotion.getUsageLimit() - 1);
        }

        // Giảm sức chứa của tour
        tour.setCapacity(tour.getCapacity() - totalPeople);

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
                .user(user)
                .build();

        // Liên kết các participant với booking
        booking.setParticipantsAndLink(req.getParticipants());

        Booking savedBooking = bookingRepo.save(booking);

        if (Boolean.TRUE.equals(req.getIsCashPayment())) {
            try {
                String subject = "Xác nhận đặt tour " + savedBooking.getTour().getTitle();
                String htmlContent = emailService.buildBookingConfirmationHtml(
                        savedBooking.getUserName(),
                        savedBooking.getTour().getTitle(),
                        savedBooking.getId().toString(),
                        savedBooking.getTotalPrice()
                );
                emailService.sendBookingConfirmationEmail(savedBooking.getEmail(), subject, htmlContent);
            } catch (MessagingException e) {
                System.err.println("LỖI: Không thể gửi email xác nhận cho booking " + savedBooking.getId() + ": " + e.getMessage());
            }
            return mapToBookingResponse(savedBooking);
        } else {
            return vnPayService.createPaymentUrl(savedBooking, baseUrl);
        }
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
            String receiverEmail = (user == null) ? booking.getGuestEmail() : user.getEmail();

            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendBookingConfirmationEmail(
                            receiverEmail,
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

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidDataException("Đơn này đã được hủy trước đó.");
        }
        if (booking.getRefund() != null) {
            throw new InvalidDataException("Đơn này đã có thông tin hoàn tiền, không thể hủy lại.");
        }

        Tour tour = booking.getTour();
        Instant now = Instant.now();
        LocalDateTime startDate = tour.getStartDate();
        Instant startAt = startDate.atZone(ZoneId.systemDefault()).toInstant();

        if (now.isAfter(startAt) || now.equals(startAt)) {
            throw new InvalidDataException("Không thể hủy đặt tour vì tour đã bắt đầu.");
        }

        long hoursBefore = Duration.between(now, startAt).toHours();
        Instant endAt = tour.getEndDate().atZone(ZoneId.systemDefault()).toInstant();
        boolean isHoliday = holidayService.hasHolidayInRange(startAt, endAt);

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

        Booking bookingFull = bookingRepo.findFullById(bookingId)
                .orElseThrow(() -> new InvalidDataException("Booking not found after update"));

        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendBookingCancelEmail(bookingFull, bookingFull.getTotalPrice(), penaltyPercent, refundAmount);
            } catch (Exception e) {
                System.err.println("Failed to send cancellation email for booking " + bookingFull.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });

        return new CancelResponse(booking.getId(), total, penaltyPercent, refundAmount);
    }

    public BookingResponse revertBookingCancellation(Long bookingId) {
        // 1. Tìm đơn hàng và các thông tin liên quan
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new InvalidDataException("Không tìm thấy đơn đặt tour với ID: " + bookingId));

        Tour tour = booking.getTour();

        // 2. Kiểm tra các điều kiện tiên quyết
        // Đơn hàng phải ở trạng thái "CANCELLED" mới có thể khôi phục.
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new InvalidDataException("Chỉ có thể khôi phục các đơn hàng đã bị hủy.");
        }

        // Nếu refund đã được xử lý (đã trả tiền) thì không thể khôi phục.
        Refund refund = booking.getRefund();
        if (refund != null && refund.getStatus() != RefundStatus.IN_PROCESS) {
            throw new InvalidDataException("Không thể khôi phục vì yêu cầu hoàn tiền đã được xử lý.");
        }

        // Kiểm tra xem tour đã bắt đầu hay chưa.
        Instant now = Instant.now();
        Instant startAt = tour.getStartDate().atZone(ZoneId.systemDefault()).toInstant();
        if (now.isAfter(startAt)) {
            throw new InvalidDataException("Không thể khôi phục vì tour đã bắt đầu hoặc đã kết thúc.");
        }

        // Kiểm tra sức chứa còn lại của tour.
        int requiredCapacity = booking.getAdults() + booking.getChildren();
        if (tour.getCapacity() < requiredCapacity) {
            throw new InvalidDataException("Không thể khôi phục vì tour đã hết chỗ. Sức chứa còn lại: " + tour.getCapacity());
        }

        // 3. Cập nhật thông tin đơn hàng (thực hiện logic ngược lại với cancel)
        // Cập nhật lại sức chứa của tour (giảm đi số khách của đơn này).
        tour.setCapacity(tour.getCapacity() - requiredCapacity);

        // Cập nhật trạng thái đơn hàng.
        // Dùng trạng thái REINSTATED để ghi nhận, hoặc CONFIRMED nếu muốn quay về như cũ.
        booking.setStatus(BookingStatus.CONFIRMED);

        booking.setCancelDate(null); // Xóa ngày hủy

        // Xóa thông tin hoàn tiền.
        booking.setRefund(null);

        // 4. Lưu thay đổi vào cơ sở dữ liệu
        Booking updatedBooking = bookingRepo.save(booking);

        // 5. Gửi email thông báo cho khách hàng (tùy chọn)
        Booking bookingFull = bookingRepo.findFullById(bookingId)
                .orElseThrow(() -> new InvalidDataException("Booking not found after update"));

        CompletableFuture.runAsync(() -> {
            try {
                // Bạn cần tạo một phương thức email mới cho việc này
                emailService.sendBookingReinstatedEmail(bookingFull);
            } catch (Exception e) {
                System.err.println("Failed to send reinstatement email for booking " + bookingFull.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 6. Trả về kết quả sau khi chuyển đổi qua DTO
        return mapToBookingResponse(updatedBooking);
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
        // Lấy thông tin RefundDTO, sẽ là null nếu booking.getRefund() là null
        RefundDTO refundDto = (booking.getRefund() != null)
                ? RefundDTO.mapToDto(booking.getRefund())
                : null;

        return BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getUserName())
                .customerEmail(booking.getEmail())
                .customerPhone(booking.getUser() != null ? booking.getUser().getPhone() : booking.getGuestPhone())

                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .bookingAt(booking.getBookingDate())
                .promotionDto(PromotionDTO.fromDomain(booking.getPromotion()))

                .participants(booking.getParticipants())

                .cancelAt(booking.getCancelDate())
                .tour(
                        BookingResponse.TourDto.builder()
                                .tourId(booking.getTour().getId())
                                .title(booking.getTour().getTitle())
                                .endDate(booking.getTour().getEndDate())
                                .startDate(booking.getTour().getStartDate())
                                .build()
                )
                .refund(refundDto)
                .build();
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
