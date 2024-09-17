package com.budget.api.budget_api.budget.repo;

import com.budget.api.budget_api.budget.entity.Budget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long>,
    JpaSpecificationExecutor<Budget> {
}
