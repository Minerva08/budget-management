package com.budget.api.budget_api.expense.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ExpenseModReq {
    private List<ExpenseMod> modList;

    @Getter
    @Setter
    @Valid
    public static class ExpenseMod {
        @NotNull(message = "mod ExpenseId is not Null")
        private Long expenseId;
        @Nullable
        private String modeCategoryCode;
        @Nullable
        private Long modExpense;
        @Nullable
        private String memo;
    }

}
