package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Promotion;
import com.dinhngoctranduy.model.request.PromotionRequest;
import com.dinhngoctranduy.model.response.PromotionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromotionService {
    List<PromotionResponse> getAll(Pageable pageable);

    PromotionResponse getById(Long id);

    PromotionResponse create(PromotionRequest request);

    PromotionResponse update(Long id, PromotionRequest request);

    void delete(Long id);

    PromotionResponse getByCode(String code);

    List<PromotionResponse> searchByDescription(String keyword);

    List<PromotionResponse> getCustom(Pageable pageable);

    Promotion getValidPromotionByCode(String code);
}