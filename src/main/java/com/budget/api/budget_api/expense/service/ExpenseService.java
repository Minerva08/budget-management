package com.budget.api.budget_api.expense.service;

import com.budget.api.budget_api.expense.dto.ExpenseModReq;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import jakarta.validation.Valid;

public interface ExpenseService {

    ExpenseRes registerExpense(@Valid ExpenseReq expenseReq, String userAccount);

    ExpenseRes updateExpense(@Valid ExpenseModReq expenseModReq, String userAccount);
}
