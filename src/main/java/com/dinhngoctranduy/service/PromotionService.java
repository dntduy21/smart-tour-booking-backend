package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Promotion;
import com.dinhngoctranduy.model.dto.PromotionRequest;
import com.dinhngoctranduy.model.dto.PromotionResponse;

import java.util.List;

public interface PromotionService {
    List<PromotionResponse> getAll();
    PromotionResponse getById(Long id);
    PromotionResponse create(PromotionRequest request);
    PromotionResponse update(Long id, PromotionRequest request);
    void delete(Long id);
    PromotionResponse getByCode(String code);
    List<PromotionResponse> searchByDescription(String keyword);

}