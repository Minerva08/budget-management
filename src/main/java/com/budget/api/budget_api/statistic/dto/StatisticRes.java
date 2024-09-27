package com.budget.api.budget_api.statistic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatisticRes {
    private String standard;
    private List<Statistic> statisticList;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class Statistic{
        @JsonInclude(Include.NON_NULL)
        private String message;
        @JsonInclude(Include.NON_NULL)
        private String categoryCode;
        @JsonInclude(Include.NON_NULL)
        private String categoryName;
        @JsonInclude(Include.NON_NULL)
        private Double rate;
        private final String unit = "%";
    }

}
