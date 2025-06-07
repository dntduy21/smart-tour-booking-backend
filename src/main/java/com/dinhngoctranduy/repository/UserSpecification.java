package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction(); // always true
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("fullName")), likePattern),
                cb.like(cb.lower(root.get("email")), likePattern),
                cb.like(cb.lower(root.get("phone")), likePattern)
            );
        };
    }

    public static Specification<User> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }
}
