package com.budget.api.budget_api.budget.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BudgetReq {
    private List<GoalBudget> budgetList;
    private String startDate;
    private String endDate;

    @Builder
    @Getter
    public static class GoalBudget{
        @NotNull(message = "categoryCode is not null")
        private String categoryCode;
        @NotNull(message = "budget is not null")
        private Long budget;
    }

    public BudgetReq(List<GoalBudget> budgetList, String startDate, String endDate) {
        this.budgetList = budgetList;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
