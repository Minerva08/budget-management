package com.budget.api.budget_api.statistic.repo;

import com.budget.api.budget_api.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository extends JpaRepository<Expense,Long> ,CustomStatistic{

}
