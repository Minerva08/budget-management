package com.budget.api.budget_api.budget.service;

import com.budget.api.budget_api.budget.dto.BudgetListRes;
import com.budget.api.budget_api.budget.dto.BudgetReq;
import com.budget.api.budget_api.budget.dto.BudgetRes;
import jakarta.validation.Valid;

public interface BudgetService {

    BudgetRes registerBudgetByUser(@Valid BudgetReq budgeInfo, String userAccount);

    BudgetListRes getBudgetList(String startDate,String endDate,Long budgetMin,Long budgetMax,String category, String userAccount);
}
