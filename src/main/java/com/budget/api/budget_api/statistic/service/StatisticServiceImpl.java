package com.budget.api.budget_api.statistic.service;

import com.budget.api.budget_api.category.repo.CategoryRepository;
import com.budget.api.budget_api.expense.dto.ExpenseSearch;
import com.budget.api.budget_api.expense.entity.Expense;
import com.budget.api.budget_api.expense.repo.ExpenseRepository;
import com.budget.api.budget_api.expense.repo.ExpenseSpecification;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.util.DateUtil;
import com.budget.api.budget_api.statistic.dto.ExpenseStatisticDto;
import com.budget.api.budget_api.statistic.dto.StatisticRes;
import com.budget.api.budget_api.statistic.dto.StatisticRes.Statistic;
import com.budget.api.budget_api.statistic.dto.StatisticSearch;
import com.budget.api.budget_api.statistic.enums.StatisticStandard;
import com.budget.api.budget_api.statistic.repo.StatisticRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService{

    private final StatisticRepository statisticRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public StatisticRes getStatistic(StatisticStandard standard, String subStandard, String nowDate,
        String userAccount) {

        StatisticSearch search = StatisticSearch.builder()
            .type(standard)
            .categoryCode(subStandard)
            .account(userAccount)
            .nowDate(DateUtil.convertStringToDate(nowDate))
            .build();

        List<ExpenseStatisticDto> statisticExpenseList = statisticRepository.getExpenseStatistics(search);

        log.info("statistic List :{}",statisticExpenseList);

        switch (standard){
            case MONTH -> {
                Map<String, List<ExpenseStatisticDto>> groupByCat = statisticExpenseList.stream()
                    .collect(Collectors.groupingBy(ExpenseStatisticDto::getCategoryCode));

                List<Statistic> list = new ArrayList<>();

                for (Entry<String, List<ExpenseStatisticDto>> cateListEntry : groupByCat.entrySet()) {

                    if(cateListEntry.getValue().size()==2){
                        Statistic statistic = Statistic.builder()
                            .rate((double) ((cateListEntry.getValue().get(1).getExpense()/cateListEntry.getValue().get(0).getExpense())*100))
                            .categoryCode(cateListEntry.getKey())
                            .categoryName(cateListEntry.getValue().get(0).getCategoryName())
                            .build();
                        list.add(statistic);
                    }else{

                        Statistic statistic = Statistic.builder()
                            .categoryCode(cateListEntry.getKey())
                            .categoryName(categoryRepository.findByCategoryCode(cateListEntry.getKey()).orElseThrow(()->new CustomException(
                                    ErrorCode.BUDGET_CATE_NOT_EXIST)).getCategoryName())
                            .message("이전 소비 정보가 없습니다.")
                            .build();
                        list.add(statistic);
                    }
                }
                return StatisticRes.builder()
                    .standard(String.valueOf(search.getType()))
                    .statisticList(list)
                    .build();

            }
            case WEEKDAY -> {

                ExpenseSearch expenseSearch = ExpenseSearch.builder()
                    .userAccount(userAccount)
                    .startDate(LocalDate.from(DateUtil.convertStringToDate(nowDate).atTime(0,0,0)))
                    .build();

                Specification<Expense> expenseSpec = ExpenseSpecification.hasCondition(expenseSearch);
                List<Expense> nowExpenseList = expenseRepository.findAll(expenseSpec);

                if(nowExpenseList.isEmpty()) {
                    return StatisticRes.builder()
                        .standard(StatisticStandard.WEEKDAY.name())
                        .statisticList(List.of(Statistic.builder()
                                .message("이전 소비 정보가 없습니다")
                            .build()))
                        .build();
                }

                Long nowExpense = nowExpenseList.get(0).getExpense();

                Long compareExpense = statisticExpenseList.stream()
                    .mapToLong(ExpenseStatisticDto::getExpense).sum();
                double rate = (compareExpense == 0) ? 0 : ((double) nowExpense / compareExpense) * 100;


                return StatisticRes.builder()
                    .standard(StatisticStandard.WEEKDAY.name())
                    .statisticList(
                        List.of(
                            Statistic.builder()
                            .rate(rate)
                            .build()
                        )
                    )
                    .build();

            }
            case USERS -> {

            }
        }


        return null;
    }
}
