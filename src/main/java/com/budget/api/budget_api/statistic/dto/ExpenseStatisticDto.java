package com.budget.api.budget_api.statistic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ExpenseStatisticDto {
    private String account;
    private String weekDay;
    private String month;
    private String categoryCode;
    private String categoryName;
    private Long expense;

    public ExpenseStatisticDto(String account, String weekDay, String month, String categoryCode,
        String categoryName, Long expense) {
        this.account = account;
        this.weekDay = weekDay;
        this.month = month;
        this.categoryCode = categoryCode;
        this.expense = expense;
        this.categoryName = categoryName;
    }

}
