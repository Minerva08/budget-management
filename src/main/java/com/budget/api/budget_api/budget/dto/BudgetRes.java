package com.budget.api.budget_api.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BudgetRes {
    private String account;
    private int updateCnt;

}
