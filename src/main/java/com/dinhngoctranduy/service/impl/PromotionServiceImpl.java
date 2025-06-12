package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.Promotion;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.dto.PromotionRequest;
import com.dinhngoctranduy.model.dto.PromotionResponse;
import com.dinhngoctranduy.repository.PromotionRepository;
import com.dinhngoctranduy.service.EmailService;
import com.dinhngoctranduy.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final EmailService emailService;

    @Override
    public List<PromotionResponse> getAll() {
        return promotionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getById(Long id) {
        return toResponse(promotionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Promotion not found")));
    }

    @Override
    public PromotionResponse create(PromotionRequest request) {
        validateRequest(request);
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Promotion code already exists");
        }
        Promotion promotion = toEntity(request);
        return toResponse(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponse update(Long id, PromotionRequest request) {
        validateRequest(request);
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Promotion not found"));

        if (!existing.getCode().equals(request.getCode()) && promotionRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Promotion code already exists");
        }

        existing.setCode(request.getCode());
        existing.setDescription(request.getDescription());
        existing.setDiscountPercent(request.getDiscountPercent());
        existing.setStartAt(request.getStartAt());
        existing.setEndAt(request.getEndAt());
        existing.setUsageLimit(request.getUsageLimit());

        return toResponse(promotionRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Promotion promotion = promotionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NoSuchElementException("Promotion not found"));
        promotion.setActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    public PromotionResponse getByCode(String code) {
        Promotion promotion = promotionRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new NoSuchElementException("Promotion not found"));
        return toResponse(promotion);

    }

    @Override
    public List<PromotionResponse> searchByDescription(String keyword) {
        return promotionRepository.findByDescriptionContainingIgnoreCase(keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validateRequest(PromotionRequest request) {
        if (request.getStartAt() != null && request.getEndAt() != null &&
                request.getStartAt().isAfter(request.getEndAt())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private Promotion toEntity(PromotionRequest request) {
        return Promotion.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountPercent(request.getDiscountPercent())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .usageLimit(request.getUsageLimit())
                .active(true)
                .build();
    }

    private PromotionResponse toResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .discountPercent(promotion.getDiscountPercent())
                .startAt(promotion.getStartAt())
                .endAt(promotion.getEndAt())
                .usageLimit(promotion.getUsageLimit())
                .active(promotion.isActive())
                .build();
    }

    public void createAndSendWelcomePromotion(User user) {
        CompletableFuture.runAsync(() -> {
            try {
                String promotionCode = "WELCOME_" + user.getId();

                if (promotionRepository.existsByCode(promotionCode)) {
                    return;
                }

                PromotionRequest welcomeRequest = PromotionRequest.builder()
                        .code(promotionCode)
                        .description("Ưu đãi đặc biệt dành cho thành viên mới của DuyTour!")
                        .discountPercent(15.0) // Giảm 15%
                        .startAt(Instant.now())
                        .endAt(Instant.now().plus(7, ChronoUnit.DAYS)) // Hết hạn sau 7 ngày
                        .usageLimit(1) // Dùng 1 lần
                        .build();

                this.create(welcomeRequest);

                String subject = "🎁 Quà chào mừng từ DuyTour! Mã khuyến mãi của bạn";
                String content = """
                        <h3>Cảm ơn bạn đã tham gia cùng DuyTour!</h3>
                        <p>Để chào mừng thành viên mới, chúng tôi xin gửi tặng bạn một mã khuyến mãi đặc biệt:</p>
                        <p><strong>Mã khuyến mãi:</strong> %s</p>
                        <p><strong>Mô tả:</strong> %s</p>
                        <p><strong>Giảm giá:</strong> %.0f%%</p>
                        <p><strong>Hiệu lực:</strong> Mã có giá trị trong 7 ngày kể từ hôm nay.</p>
                        <p>Hãy nhanh tay đặt tour và nhập mã khuyến mãi khi thanh toán để nhận ưu đãi nhé!</p>
                        <hr>
                        <p style="font-size:12px;">Đây là email tự động. Vui lòng không phản hồi.</p>
                        """.formatted(
                        welcomeRequest.getCode(),
                        welcomeRequest.getDescription(),
                        welcomeRequest.getDiscountPercent()
                );

                emailService.sendHtmlEmail(user.getEmail(), subject, content);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void createAndSendBirthdayPromotion(User user) {
        CompletableFuture.runAsync(() -> {
            try {
                int currentYear = LocalDate.now().getYear();
                String promotionCode = String.format("BDAY-%d-%d", user.getId(), currentYear);

                // Kiểm tra xem năm nay đã gửi quà cho user này chưa
                if (promotionRepository.existsByCode(promotionCode)) {
                    return;
                }

                PromotionRequest birthdayRequest = PromotionRequest.builder()
                        .code(promotionCode)
                        .description("Quà mừng sinh nhật từ DuyTour! Chúc bạn một tuổi mới nhiều niềm vui và những chuyến đi thú vị.")
                        .discountPercent(25.0) // Giảm 25%, bạn có thể thay đổi
                        .startAt(Instant.now())
                        .endAt(Instant.now().plus(30, ChronoUnit.DAYS)) // Hạn dùng 30 ngày
                        .usageLimit(1) // Dùng 1 lần
                        .build();

                this.create(birthdayRequest);

                String subject = "🎂 Chúc mừng sinh nhật! DuyTour gửi tặng bạn món quà đặc biệt";
                String content = """
                        <h3>Chúc mừng sinh nhật, %s!</h3>
                        <p>Nhân ngày đặc biệt của bạn, DuyTour xin gửi lời chúc tốt đẹp nhất và một món quà nhỏ để bạn có thêm niềm vui trong những chuyến đi sắp tới:</p>
                        <p><strong>Mã khuyến mãi:</strong> %s</p>
                        <p><strong>Ưu đãi:</strong> Giảm ngay %.0f%% cho lần đặt tour tiếp theo.</p>
                        <p><strong>Mô tả:</strong> %s</p>
                        <p><strong>Hiệu lực:</strong> Mã có giá trị trong 30 ngày.</p>
                        <p>Chúc bạn một tuổi mới thật ý nghĩa và có nhiều hành trình đáng nhớ!</p>
                        <hr>
                        <p style="font-size:12px;">Đây là email tự động. Vui lòng không phản hồi.</p>
                        """.formatted(
                        user.getFullName() != null ? user.getFullName() : user.getUsername(),
                        birthdayRequest.getCode(),
                        birthdayRequest.getDiscountPercent(),
                        birthdayRequest.getDescription()
                );

                emailService.sendHtmlEmail(user.getEmail(), subject, content);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}