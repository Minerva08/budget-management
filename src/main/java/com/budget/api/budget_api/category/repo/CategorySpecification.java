package com.budget.api.budget_api.category.repo;

import com.budget.api.budget_api.category.entity.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> hasNameOrCode(String search) {
        return (root, query, builder) -> {
            if (search == null || search.isEmpty()) {
                return builder.conjunction();  // 빈 검색어일 경우 전체 검색
            }
            String searchPattern = "%" + search + "%";
            return builder.or(
                builder.like(root.get("categoryName"), searchPattern),
                builder.like(root.get("categoryCode"), searchPattern)
            );
        };
    }
}
