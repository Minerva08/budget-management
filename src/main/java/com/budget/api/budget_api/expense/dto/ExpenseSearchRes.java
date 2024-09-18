package com.budget.api.budget_api.expense.dto;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExpenseSearchRes {
    private String account;
    private int expenseCnt;
    private long totalExpense;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SumExpense> sumExpense;

    @Getter
    @Setter
    public static class SumExpense{
        private String categoryCode;
        private String categoryName;
        private List<ExpenseDetail> expenseList;
        private long totalByCategory;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ExpenseDetail{
        private Long expense;
        private String memo;
        private LocalDateTime createdTime;

    }
}
