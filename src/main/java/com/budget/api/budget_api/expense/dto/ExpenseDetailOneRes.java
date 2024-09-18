package com.budget.api.budget_api.expense.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ExpenseDetailOneRes extends ExpenseDetail{
    private String categoryCode;
    private String categoryName;
    private String account;

    public ExpenseDetailOneRes(Long expense, String memo, LocalDateTime createdTime,
        String categoryCode, String categoryName, String account) {
        super(expense, memo, createdTime);
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.account = account;
    }
}
