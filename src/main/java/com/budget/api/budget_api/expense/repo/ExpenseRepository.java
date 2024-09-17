package com.budget.api.budget_api.expense.repo;

import com.budget.api.budget_api.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long>,
    JpaSpecificationExecutor<Expense> {
}
