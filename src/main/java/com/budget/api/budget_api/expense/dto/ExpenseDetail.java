package com.budget.api.budget_api.expense.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class ExpenseDetail{
    private Long expense;
    private String memo;
    private LocalDateTime createdTime;
    private Boolean isExcludingTotal;

}
