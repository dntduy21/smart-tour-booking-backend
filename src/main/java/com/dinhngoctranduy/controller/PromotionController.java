package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.PromotionRequest;
import com.dinhngoctranduy.model.dto.PromotionResponse;
import com.dinhngoctranduy.model.dto.SendPromotionEmailRequest;
import com.dinhngoctranduy.service.EmailService;
import com.dinhngoctranduy.service.PromotionService;
import com.dinhngoctranduy.util.SuccessPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;
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
    public ResponseEntity<List<PromotionResponse>> getAll() {
        return ResponseEntity.ok(promotionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getById(id));
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
                <h3>🎁 Ưu đãi đặc biệt từ DuyTour!</h3>
                <p><strong>Mã khuyến mãi:</strong> %s</p>
                <p><strong>Mô tả:</strong> %s</p>
                <p><strong>Giảm giá:</strong> %.0f%%</p>
                <p><strong>Thời gian áp dụng:</strong> từ %s đến %s</p>
                <p>Hãy nhanh tay đặt tour và nhập mã khuyến mãi khi thanh toán!</p>
                <hr>
                <p style="font-size:12px;">Đây là email tự động. Vui lòng không phản hồi.</p>
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
