package com.dinhngoctranduy.repository;// TourSpecification.java
import com.dinhngoctranduy.model.Review;
import com.dinhngoctranduy.model.Tour;
import com.dinhngoctranduy.model.request.TourSearchRequest;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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

            if (request.getMinRating() != null || request.getMaxRating() != null) {
                // 1. Tạo một subquery để chọn các tour ID có rating trung bình phù hợp
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Review> subRoot = subquery.from(Review.class); // Bắt đầu từ bảng Review

                // 2. Chọn ra tour_id từ bảng Review
                subquery.select(subRoot.get("tour").get("id"));

                // 3. Nhóm các kết quả theo tour_id
                subquery.groupBy(subRoot.get("tour").get("id"));

                // 4. Tạo các điều kiện cho mệnh đề HAVING
                List<Predicate> havingPredicates = new ArrayList<>();
                Expression<Double> avgRating = cb.avg(subRoot.get("rating")); // Tính rating trung bình

                if (request.getMinRating() != null) {
                    havingPredicates.add(cb.greaterThanOrEqualTo(avgRating, request.getMinRating().doubleValue()));
                }
                if (request.getMaxRating() != null) {
                    havingPredicates.add(cb.lessThanOrEqualTo(avgRating, request.getMaxRating().doubleValue()));
                }

                // 5. Áp dụng các điều kiện vào mệnh đề HAVING của subquery
                subquery.having(cb.and(havingPredicates.toArray(new Predicate[0])));

                // 6. Thêm điều kiện vào truy vấn chính: tour.id phải nằm trong danh sách ID từ subquery
                predicates.add(root.get("id").in(subquery));
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
