package com.budget.api.budget_api.budget.repo;

import com.budget.api.budget_api.budget.entity.Budget;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class BudgetSpecification {

    public static Specification<Budget> hasCondition(String categoryCode,Long budgetMin, Long budgetMax, LocalDate startDate, LocalDate endDate) {
        return (root, query, builder) -> {
            // 기본 조건을 conjunction으로 시작
            Predicate predicate = builder.conjunction();

            // categoryCode 조건 추가
            if (categoryCode != null && !categoryCode.isEmpty()) {
                String searchPattern = "%" + categoryCode + "%";
                predicate = builder.and(predicate, builder.like(root.get("categoryCode"), searchPattern));
            }

            // budget 범위 조건 추가
            if (budgetMin != null && budgetMax != null) {
                predicate = builder.and(predicate, builder.between(root.get("budgetAmount"), budgetMin, budgetMax));
            }

            if(startDate==null && endDate !=null){
                predicate = builder.and(predicate,builder.lessThanOrEqualTo(root.get("endDate"),endDate));
            }

            if(startDate!=null&& endDate==null){
                predicate = builder.and(predicate,builder.greaterThanOrEqualTo(root.get("startDate"),startDate));
            }

            if(startDate!=null && endDate!=null){
                predicate = builder.and(predicate,builder.greaterThanOrEqualTo(root.get("startDate"), startDate)); // 이상
                predicate = builder.and(predicate,builder.lessThanOrEqualTo(root.get("endDate"), startDate)); // 이하
            }

            return predicate;
        };
    }
}


