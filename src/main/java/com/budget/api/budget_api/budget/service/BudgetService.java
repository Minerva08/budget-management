package com.budget.api.budget_api.budget.service;

import com.budget.api.budget_api.budget.dto.BudgetReq;
import com.budget.api.budget_api.budget.dto.BudgetRes;
import jakarta.validation.Valid;

public interface BudgetService {

    BudgetRes registerBudgetByUser(@Valid BudgetReq budgeInfo, String userAccount, String username);
}
