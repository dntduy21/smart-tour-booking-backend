package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.Promotion;
import com.dinhngoctranduy.model.dto.PromotionRequest;
import com.dinhngoctranduy.model.dto.PromotionResponse;
import com.dinhngoctranduy.repository.PromotionRepository;
import com.dinhngoctranduy.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

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
}