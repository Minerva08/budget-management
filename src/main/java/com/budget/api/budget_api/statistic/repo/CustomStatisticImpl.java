package com.budget.api.budget_api.statistic.repo;

import com.budget.api.budget_api.category.entity.Category;
import com.budget.api.budget_api.expense.entity.Expense;
import com.budget.api.budget_api.statistic.dto.ExpenseStatisticDto;
import com.budget.api.budget_api.statistic.dto.StatisticSearch;
import com.budget.api.budget_api.user.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;

public class CustomStatisticImpl implements CustomStatistic {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<ExpenseStatisticDto> getExpenseStatistics(StatisticSearch search) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExpenseStatisticDto> criteriaQuery = criteriaBuilder.createQuery(ExpenseStatisticDto.class);
        Root<Expense> root = criteriaQuery.from(Expense.class);


        Predicate betweenNowDateCondition = null;
        // LocalDateTime을 Timestamp로 변환
        if(search.getNowStartDate()!=null && search.getNowEndDate()!=null){
            Timestamp nowStartTimestamp = Timestamp.valueOf(search.getNowStartDate());
            Timestamp nowEndTimestamp = Timestamp.valueOf(search.getNowEndDate());
            betweenNowDateCondition = criteriaBuilder.between(root.get("creatDate"), nowStartTimestamp, nowEndTimestamp);

        }
        Timestamp targetStartTimestamp = Timestamp.valueOf(search.getTargetStartDate());
        Timestamp targetEndTimestamp = Timestamp.valueOf(search.getTargetEndDate());

        // BETWEEN 조건 생성
        Predicate betweenTargetDateCondition = criteriaBuilder.between(root.get("creatDate"), targetStartTimestamp, targetEndTimestamp);

        // 최종 WHERE 절 적용
        Predicate finalPredicate = null;

        if(betweenNowDateCondition==null){
            finalPredicate = betweenTargetDateCondition;
        }else{
            finalPredicate = criteriaBuilder.or(betweenNowDateCondition, betweenTargetDateCondition);
        }

        switch (search.getType()){
            case MONTH -> {
                // Join category table only for MONTH case
                Join<Expense, Category> categoryJoin = root.join("category", JoinType.LEFT);
                Join<Expense, Member> memberJoin = root.join("member", JoinType.LEFT);// Assuming "category" is the name of the association field in Expense entity

                // Filter by categoryCode
                if(search.getCategoryCode()!=null){
                    finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(categoryJoin.get("categoryCode"), search.getCategoryCode()));

                }
                finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(memberJoin.get("account"), search.getAccount()));

                criteriaQuery.where(finalPredicate);

                // 월별 계산을 위한 필드 (가정: `date` 필드가 있음)
                criteriaQuery.multiselect(
                    criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%m")).alias("month"),
                    categoryJoin.get("categoryCode").alias("categoryCode"),
                    categoryJoin.get("categoryName").alias("categoryName"),
                    criteriaBuilder.sum(root.get("expense")).alias("expense")
                );

                criteriaQuery.groupBy(
                    criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%m")),
                    categoryJoin.get("categoryCode")
                );
            }
            case WEEKDAY -> {

                finalPredicate = criteriaBuilder.and(
                    finalPredicate,
                    criteriaBuilder.equal(
                        criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%w")),
                        search.getWeekDayNum().toString() // 요일 숫자를 문자열로 변환
                    )
                );

                criteriaQuery.where(finalPredicate);

                // 요별 계산을 위한 필드 (가정: `date` 필드가 있음)
                criteriaQuery.multiselect(
                    criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%w")).alias("weekDay"),
                    criteriaBuilder.sum(root.get("expense")).alias("expense")
                );

                criteriaQuery.groupBy(
                    criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%w"))

                );

            }
            case USERS -> {
                criteriaQuery.where(finalPredicate);

                criteriaQuery.multiselect(
                    criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%m")).alias("month"),
                    root.get("account").alias("account"),
                    criteriaBuilder.sum(root.get("expense")).alias("expense")
                );

                criteriaQuery.groupBy(
                    criteriaBuilder.function("DATE_FORMAT", String.class, root.get("creatDate"), criteriaBuilder.literal("%m")),
                    root.get("account")
                );

            }
        }


        // 쿼리 실행 및 결과 반환
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
