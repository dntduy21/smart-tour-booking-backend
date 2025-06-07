package com.dinhngoctranduy.repository;// TourSpecification.java
import com.dinhngoctranduy.model.Tour;
import com.dinhngoctranduy.model.dto.TourSearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TourSpecification {

    public static Specification<Tour> search(TourSearchRequest request, Boolean isDeleted) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword: title, description, destination
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("destination")), keyword)
                ));
            }

            // Thời gian khởi hành
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), request.getStartDate().atStartOfDay()));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), request.getEndDate().atTime(23, 59, 59)));
            }

            if (request.getLocation() != null && !request.getLocation().isBlank()) {
                predicates.add(cb.equal(root.get("region"), request.getLocation()));
            }

            // Giá người lớn
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("priceAdults"), request.getMinPrice()));
            }

            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("priceAdults"), request.getMaxPrice()));
            }

            // Chỉ tour còn hoạt động
            predicates.add(cb.isTrue(root.get("available")));
            if(isDeleted != null) {
                predicates.add(cb.isFalse(root.get("deleted")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
