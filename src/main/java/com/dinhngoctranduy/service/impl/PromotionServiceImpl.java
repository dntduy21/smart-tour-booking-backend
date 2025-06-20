package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.Promotion;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.dto.PromotionRequest;
import com.dinhngoctranduy.model.dto.PromotionResponse;
import com.dinhngoctranduy.repository.PromotionRepository;
import com.dinhngoctranduy.service.EmailService;
import com.dinhngoctranduy.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public List<PromotionResponse> getAll(Pageable pageable) {
        return promotionRepository.findAll(pageable).getContent()
                .stream()
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
                        .description("Ưu đãi đặc biệt dành cho thành viên mới của SmartTour!")
                        .discountPercent(15.0)
                        .startAt(Instant.now())
                        .endAt(Instant.now().plus(7, ChronoUnit.DAYS))
                        .usageLimit(1)
                        .build();

                this.create(welcomeRequest);

                String subject = "🎁 Quà chào mừng từ SmartTour! Mã khuyến mãi của bạn";
                String content = """
                        <!DOCTYPE html>
                        <html lang="vi">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                body { font-family: 'Helvetica Neue', Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
                                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
                                .header { text-align: center; padding: 20px; }
                                .header img.logo { max-width: 150px; }
                                .banner img { width: 100%%; height: auto; }
                                .content-body { padding: 20px 30px; }
                                .content-body h1 { color: #2c3e50; font-size: 24px; }
                                .content-body p { color: #34495e; font-size: 16px; line-height: 1.6; }
                                .promo-box { background-color: #e8f6f3; border: 2px dashed #1abc9c; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }
                                .promo-box .code { font-size: 28px; font-weight: bold; color: #16a085; letter-spacing: 2px; }
                                .promo-box .description { font-size: 14px; color: #34495e; margin-top: 10px; }
                                .cta-button { display: inline-block; background-color: #3498db; color: #ffffff; padding: 15px 30px; border-radius: 5px; text-decoration: none; font-size: 18px; font-weight: bold; margin-top: 20px; }
                                .footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px; text-align: center; font-size: 12px; }
                                .footer p { margin: 5px 0; }
                                .footer a { color: #3498db; text-decoration: none; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="content-body">
                                    <h1>Chào mừng bạn đến với SmartTour!</h1>
                                    <p>Cảm ơn bạn đã tham gia gia nhập website du lịch của chúng tôi. Để khởi đầu cho những hành trình tuyệt vời sắp tới, SmartTour xin gửi tặng bạn một món quà làm quen đặc biệt.</p>
                        
                                    <div class="promo-box">
                                        <p style="margin:0; font-size: 16px; color: #34495e;">Mã khuyến mãi của bạn:</p>
                                        <p class="code">%s</p>
                                        <p class="description">Giảm ngay <strong>%.0f%%</strong> cho lần đặt tour đầu tiên. <br>Hiệu lực trong vòng 7 ngày.</p>
                                    </div>
                        
                                    <p>Hãy chọn ngay một điểm đến yêu thích và sử dụng mã ưu đãi khi thanh toán nhé!</p>
                        
                                </div>
                                <div class="footer">
                                    <p>&copy; %d SmartTour. All rights reserved.</p>
                                    <p>Bạn nhận được email này vì đã đăng ký tài khoản tại SmartTour.</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """.formatted(
                        welcomeRequest.getCode(),
                        welcomeRequest.getDiscountPercent(),
                        java.time.Year.now().getValue()
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

                if (promotionRepository.existsByCode(promotionCode)) {
                    return;
                }

                PromotionRequest birthdayRequest = PromotionRequest.builder()
                        .code(promotionCode)
                        .description("Quà mừng sinh nhật từ SmartTour! Chúc bạn một tuổi mới nhiều niềm vui và những chuyến đi thú vị.")
                        .discountPercent(25.0) // Giảm 25%
                        .startAt(Instant.now())
                        .endAt(Instant.now().plus(30, ChronoUnit.DAYS)) // Hạn dùng 30 ngày
                        .usageLimit(1) // Dùng 1 lần
                        .build();

                this.create(birthdayRequest);

                String subject = "🎂 Chúc mừng sinh nhật! SmartTour gửi tặng bạn món quà đặc biệt";
                String content = """
                        <!DOCTYPE html>
                        <html lang="vi">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                body { font-family: 'Helvetica Neue', Arial, sans-serif; margin: 0; padding: 0; background-color: #f8f2f2; }
                                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
                                .header { text-align: center; padding: 20px; }
                                .header img.logo { max-width: 150px; }
                                .banner img { width: 100%%; height: auto; }
                                .content-body { padding: 20px 30px; }
                                .content-body h1 { color: #e74c3c; font-size: 26px; text-align: center; }
                                .content-body p { color: #34495e; font-size: 16px; line-height: 1.6; }
                                .promo-box { background-color: #fef5e7; border: 2px dashed #f39c12; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }
                                .promo-box .code { font-size: 28px; font-weight: bold; color: #d35400; letter-spacing: 2px; }
                                .promo-box .description { font-size: 14px; color: #34495e; margin-top: 10px; }
                                .cta-button { display: inline-block; background-color: #e74c3c; color: #ffffff; padding: 15px 30px; border-radius: 5px; text-decoration: none; font-size: 18px; font-weight: bold; margin-top: 20px; }
                                .footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px; text-align: center; font-size: 12px; }
                                .footer p { margin: 5px 0; }
                                .footer a { color: #3498db; text-decoration: none; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="content-body">
                                    <h1>Chúc Mừng Sinh Nhật, %s!</h1>
                                    <p>Nhân ngày đặc biệt của bạn, SmartTour xin gửi lời chúc tốt đẹp nhất. Chúc bạn một tuổi mới tràn ngập niềm vui, sức khỏe và có thêm thật nhiều hành trình đáng nhớ!</p>
                        
                                    <p>Để góp vui, chúng tôi xin gửi tặng bạn một món quà sinh nhật:</p>
                        
                                    <div class="promo-box">
                                        <p style="margin:0; font-size: 16px; color: #34495e;">Mã ưu đãi đặc biệt:</p>
                                        <p class="code">%s</p>
                                        <p class="description">Giảm ngay <strong>%.0f%%</strong> cho một chuyến đi bất kỳ.<br>Mã có hiệu lực trong 30 ngày.</p>
                                    </div>
                        
                                    <p>Hãy tự thưởng cho mình một chuyến đi để khởi đầu một tuổi mới thật rực rỡ nhé!</p>
                        
                                </div>
                                <div class="footer">
                                    <p>&copy; %d SmartTour. All rights reserved.</p>
                                    <p>Bạn nhận được email này vì đây là ngày sinh nhật trong hồ sơ tài khoản của bạn tại SmartTour.</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """.formatted(
                        user.getFullName() != null ? user.getFullName() : user.getUsername(),
                        birthdayRequest.getCode(),
                        birthdayRequest.getDiscountPercent(),
                        java.time.Year.now().getValue()
                );

                emailService.sendHtmlEmail(user.getEmail(), subject, content);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<PromotionResponse> getCustom(Pageable pageable) {
        return promotionRepository.findNonAutoGeneratedPromotions(pageable).getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}