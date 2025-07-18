package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.dto.TourCategoryDTO;

import java.util.List;

public interface TourCategoryService {
    List<TourCategoryDTO> getAllCategories();

    TourCategoryDTO getCategoryById(Long id);

    List<TourCategoryDTO> searchCategoriesByName(String name);

    TourCategoryDTO createCategory(TourCategoryDTO tourCategoryDTO);

    TourCategoryDTO updateCategory(Long id, TourCategoryDTO tourCategoryDTO);

    void deleteCategory(Long id);
}