package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Promotion;
import com.dinhngoctranduy.model.request.ValidatePromotionRequest;
import com.dinhngoctranduy.model.response.ValidatePromotionResponse;
import com.dinhngoctranduy.model.request.PromotionRequest;
import com.dinhngoctranduy.model.response.PromotionResponse;
import com.dinhngoctranduy.model.request.SendPromotionEmailRequest;
import com.dinhngoctranduy.service.EmailService;
import com.dinhngoctranduy.service.PromotionService;
import com.dinhngoctranduy.util.SuccessPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<PromotionResponse> create(@Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> update(@PathVariable Long id, @Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promotionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10000") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<PromotionResponse> responses = promotionService.getAll(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/custom")
    public ResponseEntity<List<PromotionResponse>> getAllCustomPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<PromotionResponse> responses = promotionService.getCustom(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getById(id));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePromotion(@RequestBody ValidatePromotionRequest request) {
        try {
            Promotion validPromotion = promotionService.getValidPromotionByCode(request.getCode());

            // Nếu không có lỗi, tạo response thành công
            ValidatePromotionResponse response = ValidatePromotionResponse.builder()
                    .valid(true)
                    .message("Áp dụng mã khuyến mãi thành công!")
                    .discountPercent(validPromotion.getDiscountPercent())
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("valid", "false");
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public List<PromotionResponse> searchPromotions(@RequestParam String keyword) {
        return promotionService.searchByDescription(keyword);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendPromotionEmail(@RequestBody @Valid SendPromotionEmailRequest request) {
        PromotionResponse promotion = promotionService.getByCode(request.getPromotionCode());
        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }
        String subject = "Nhận ngay ưu đãi từ chúng tôi: " + promotion.getCode();

        String content = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.05);">
                    <div style="text-align: center;">
                        <h2 style="color: #e63946;">🎉 Ưu Đãi Đặc Biệt Từ <span style="color: #1d3557;">SmartTour</span>!</h2>
                        <p style="font-size: 16px;">Tận hưởng chuyến đi mơ ước cùng khuyến mãi hấp dẫn!</p>
                    </div>
                
                    <div style="margin-top: 30px;">
                        <p><strong>🧾 Mã khuyến mãi:</strong> <span style="color: #457b9d; font-weight: bold;">%s</span></p>
                        <p><strong>📋 Mô tả:</strong> %s</p>
                        <p><strong>💸 Giảm giá:</strong> <span style="color: #e63946; font-weight: bold;">%.0f%%</span></p>
                        <p><strong>📅 Thời gian áp dụng:</strong> từ <strong>%s</strong> đến <strong>%s</strong></p>
                    </div>
                
                    <div style="margin-top: 30px; text-align: center;">
                        <p style="margin-top: 10px;">Nhập mã khuyến mãi khi thanh toán để nhận ưu đãi.</p>
                    </div>
                
                    <hr style="margin-top: 40px;">
                    <p style="font-size: 12px; color: #888888; text-align: center;">
                        Đây là email tự động. Vui lòng không phản hồi lại email này.
                    </p>
                </div>
                """.formatted(
                promotion.getCode(),
                promotion.getDescription(),
                promotion.getDiscountPercent(),
                promotion.getStartAt().atZone(ZoneId.systemDefault()).toLocalDate(),
                promotion.getEndAt().atZone(ZoneId.systemDefault()).toLocalDate()
        );

        request.getEmails().forEach(email -> CompletableFuture.runAsync(() -> {
            emailService.sendHtmlEmail(email, subject, content);
        }));

        return ResponseEntity.ok(SuccessPayload.build());
    }
}
