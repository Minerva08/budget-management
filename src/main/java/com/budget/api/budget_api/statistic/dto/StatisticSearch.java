package com.budget.api.budget_api.statistic.dto;

import com.budget.api.budget_api.statistic.enums.StatisticStandard;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StatisticSearch {
    private StatisticStandard type;
    private String account;
    private String categoryCode;
    private LocalDateTime nowStartDate;
    private LocalDateTime nowEndDate;
    private LocalDateTime targetStartDate;
    private LocalDateTime targetEndDate;
    private Integer weekDayNum;

    @Builder
    public StatisticSearch(StatisticStandard type, String account, String categoryCode,
        LocalDate nowDate) {
        this.type = type;
        switch (type){
            case MONTH ->{

                this.nowStartDate = nowDate.with(TemporalAdjusters.firstDayOfMonth()).atTime(0,0,0);
                this.nowEndDate = nowDate.atTime(23,59,59);
                this.targetStartDate = this.nowStartDate.minusMonths(1);
                this.targetEndDate = this.nowEndDate.minusMonths(1);
                this.account = account;
                this.categoryCode = categoryCode;

            }
            case USERS -> {
                this.nowStartDate = nowDate.atTime(0,0,0);
                this.nowEndDate = nowDate.atTime(23,59,59);

            }
            case WEEKDAY -> {
                this.account = account;
                this.weekDayNum = nowDate.getDayOfWeek().getValue();
                this.targetEndDate = nowDate.minusDays(1).atTime(23,59,59);
                this.targetStartDate = nowDate.minusMonths(6).atTime(0,0,0);
            }
        }

    }
}
