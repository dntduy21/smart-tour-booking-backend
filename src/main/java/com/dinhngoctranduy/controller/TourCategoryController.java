package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.TourCategoryDTO;
import com.dinhngoctranduy.model.response.TourResponseDTO;
import com.dinhngoctranduy.service.TourCategoryService;
import com.dinhngoctranduy.service.TourService;
import com.dinhngoctranduy.service.impl.TourCategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class TourCategoryController {

    private final TourCategoryServiceImpl categoryService;
    private final TourService tourService;

    @GetMapping
    public ResponseEntity<List<TourCategoryDTO>> getCategories(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "showAll", required = false, defaultValue = "false") boolean showAll) {

        List<TourCategoryDTO> categories;

        if (name != null && !name.isBlank()) {
            categories = categoryService.searchCategoriesByName(name);
        } else {
            if (showAll) {
                categories = categoryService.getAllCategoriesIncludingInactive();
            } else {
                categories = categoryService.getAllCategories();
            }
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourCategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<TourCategoryDTO> createCategory(@Valid @RequestBody TourCategoryDTO tourCategoryDTO) {
        TourCategoryDTO createdCategory = categoryService.createCategory(tourCategoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourCategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody TourCategoryDTO tourCategoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, tourCategoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hideCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id); // Phương thức này đã được sửa để set active = false
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreCategory(@PathVariable Long id) {
        categoryService.restoreCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tours")
    public ResponseEntity<Page<TourResponseDTO>> getToursByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100000") int size) {
        Page<TourResponseDTO> tours = tourService.getToursByCategoryId(id, page, size);
        return ResponseEntity.ok(tours);
    }
}
