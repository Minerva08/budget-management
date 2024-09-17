package com.budget.api.budget_api.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenseRes {
    private String account;
    private int updateCnt;

}
