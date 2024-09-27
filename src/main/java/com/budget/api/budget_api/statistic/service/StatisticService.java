package com.budget.api.budget_api.statistic.service;

import com.budget.api.budget_api.statistic.dto.StatisticRes;
import com.budget.api.budget_api.statistic.enums.StatisticStandard;

public interface StatisticService {

    StatisticRes getStatistic(StatisticStandard standard, String subStandard, String nowDate, String userAccount);
}
