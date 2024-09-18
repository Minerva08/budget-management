package com.budget.api.budget_api.expense.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenseSearch {
    private Long expenseId;
    private String userAccount;
    private Long costMin;
    private Long costMax;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;

}
