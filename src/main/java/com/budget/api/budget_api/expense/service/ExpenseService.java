package com.budget.api.budget_api.expense.service;

import com.budget.api.budget_api.expense.dto.ExpenseModReq;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import com.budget.api.budget_api.expense.dto.ExpenseSearchRes;
import jakarta.validation.Valid;
import java.time.LocalDate;

public interface ExpenseService {

    ExpenseRes registerExpense(@Valid ExpenseReq expenseReq, String userAccount);

    ExpenseRes updateExpense(@Valid ExpenseModReq expenseModReq, String userAccount);

    ExpenseSearchRes getExpenseList(Long costMin, Long costMax, LocalDate startDate, LocalDate endDate, String categoryCode, String userAccount);
}
