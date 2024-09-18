package com.budget.api.budget_api.expense.repo;

import com.budget.api.budget_api.expense.entity.Expense;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long>,
    JpaSpecificationExecutor<Expense> {

    @Query("SELECT e FROM Expense e JOIN e.member m JOIN e.budget b WHERE e.expenseId IN :expenseIds AND m.account = :account")
    List<Expense> findByUserBudget(
        @Param("expenseIds") List<Long> expenseIds,
        @Param("account") String account
    );
}
