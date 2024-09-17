package com.budget.api.budget_api.budget.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BudgetListRes {
    private String account;
    private List<BudgetInfo> budgetList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BudgetInfo{
        private Long budgetNum;
        private String categoryCode;
        private String categoryName;
        private Long budget;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
