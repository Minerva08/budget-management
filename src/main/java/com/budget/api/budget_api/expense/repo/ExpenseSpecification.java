package com.budget.api.budget_api.expense.repo;

import com.budget.api.budget_api.category.entity.Category;
import com.budget.api.budget_api.expense.dto.ExpenseSearch;
import com.budget.api.budget_api.expense.entity.Expense;
import com.budget.api.budget_api.user.entity.Member;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecification {

    public static Specification<Expense> hasCondition(ExpenseSearch search) {
        return (root, query, builder) -> {
            // 기본 조건을 conjunction으로 시작
            Predicate predicate = builder.conjunction();

            // userAccount 필드 조건 추가
            if (search.getUserAccount() != null && !search.getUserAccount().isEmpty()) {
                Join<Expense, Member> memberJoin = root.join("member");
                predicate = builder.and(predicate, builder.equal(memberJoin.get("account"), search.getUserAccount()));
            }

            // costMin 조건 추가
            if (search.getCostMin()!=null && search.getCostMin() > 0) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("expense"), search.getCostMin()));
            }

            // costMax 조건 추가
            if (search.getCostMax()!=null && search.getCostMax() > 0) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("expense"), search.getCostMax()));
            }

            // startDate 조건 추가
            if (search.getStartDate() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("createDate"), search.getStartDate()));
            }

            // endDate 조건 추가
            if (search.getEndDate() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("createDate"), search.getEndDate()));
            }

            // categoryId 조건 추가
            if (search.getCategoryId() != null) {
                Join<Expense, Category> categoryJoin = root.join("category");
                predicate = builder.and(predicate, builder.equal(categoryJoin.get("id"), search.getCategoryId()));
            }

            return predicate;
        };
    }
}


