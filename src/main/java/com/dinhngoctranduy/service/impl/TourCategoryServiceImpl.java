package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.TourCategory;
import com.dinhngoctranduy.model.dto.TourCategoryDTO;
import com.dinhngoctranduy.repository.TourCategoryRepository;
import com.dinhngoctranduy.service.TourCategoryService;
import com.dinhngoctranduy.util.error.InvalidDataException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourCategoryServiceImpl implements TourCategoryService {

    private final TourCategoryRepository categoryRepository;

    @Override
    public List<TourCategoryDTO> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream() // Chỉ lấy active = true
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // MỚI: Lấy tất cả category (bao gồm cả ẩn)
    public List<TourCategoryDTO> getAllCategoriesIncludingInactive() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TourCategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id));
    }

    @Override
    public List<TourCategoryDTO> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(name).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TourCategoryDTO createCategory(TourCategoryDTO tourCategoryDTO) {
        if (categoryRepository.existsByNameIgnoreCase(tourCategoryDTO.getName())) {
            throw new InvalidDataException("Tên thể loại '" + tourCategoryDTO.getName() + "' đã tồn tại.");
        }
        TourCategory category = toEntity(tourCategoryDTO);
        // Khi tạo mới, mặc định là active
        category.setActive(true);
        TourCategory savedCategory = categoryRepository.save(category);
        return toDto(savedCategory);
    }

    @Override
    public TourCategoryDTO updateCategory(Long id, TourCategoryDTO tourCategoryDTO) {
        TourCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id));

        categoryRepository.findByNameContainingIgnoreCase(tourCategoryDTO.getName()).stream()
                .filter(cat -> !cat.getId().equals(id))
                .findFirst()
                .ifPresent(cat -> {
                    throw new InvalidDataException("Tên thể loại '" + tourCategoryDTO.getName() + "' đã tồn tại.");
                });

        existingCategory.setName(tourCategoryDTO.getName());
        // Cho phép cập nhật cả trạng thái active
        existingCategory.setActive(tourCategoryDTO.isActive());
        TourCategory updatedCategory = categoryRepository.save(existingCategory);
        return toDto(updatedCategory);
    }

    // CẬP NHẬT: Thay vì xóa cứng, ta sẽ "xóa mềm" (soft-delete)
    @Override
    public void deleteCategory(Long id) {
        TourCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id));
        category.setActive(false); // Đánh dấu là không hoạt động
        categoryRepository.save(category);
    }

    // MỚI: Thêm phương thức để kích hoạt lại category
    public void restoreCategory(Long id) {
        TourCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + id));
        category.setActive(true); // Đánh dấu là hoạt động trở lại
        categoryRepository.save(category);
    }

    // --- Helper Methods (Cập nhật) ---
    private TourCategoryDTO toDto(TourCategory entity) {
        // Thêm `entity.isActive()`
        return new TourCategoryDTO(entity.getId(), entity.getName(), entity.isActive());
    }

    private TourCategory toEntity(TourCategoryDTO dto) {
        TourCategory entity = new TourCategory();
        entity.setName(dto.getName());
        // Mặc định `active` sẽ là `true` trong Entity, hoặc có thể set từ DTO nếu cần
        entity.setActive(dto.isActive());
        return entity;
    }
}
