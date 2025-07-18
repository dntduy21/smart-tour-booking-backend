package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.model.TourCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourCategoryDTO {
    private Long id;
    private String name;
    private boolean active;

    public static TourCategoryDTO fromEntity(TourCategory entity) {
        if (entity == null) return null;
        TourCategoryDTO dto = new TourCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}