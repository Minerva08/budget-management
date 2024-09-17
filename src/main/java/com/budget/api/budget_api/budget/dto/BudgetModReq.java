package com.budget.api.budget_api.budget.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BudgetModReq {
    private List<BudgetMod> modList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetMod{
        private Long budgetId;
        private Long modBudget;
    }

}
