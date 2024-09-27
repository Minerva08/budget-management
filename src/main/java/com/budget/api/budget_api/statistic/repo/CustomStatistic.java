package com.budget.api.budget_api.statistic.repo;

import com.budget.api.budget_api.statistic.dto.ExpenseStatisticDto;
import com.budget.api.budget_api.statistic.dto.StatisticSearch;
import java.util.List;

public interface CustomStatistic {

    List<ExpenseStatisticDto> getExpenseStatistics(StatisticSearch search);
}
