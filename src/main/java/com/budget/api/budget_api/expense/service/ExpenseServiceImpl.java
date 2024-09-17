package com.budget.api.budget_api.expense.service;

import com.budget.api.budget_api.budget.entity.Budget;
import com.budget.api.budget_api.budget.repo.BudgetRepository;
import com.budget.api.budget_api.budget.repo.BudgetSpecification;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import com.budget.api.budget_api.expense.entity.Expense;
import com.budget.api.budget_api.expense.repo.ExpenseRepository;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.util.DateUtil;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.TemporalAdjusters;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;


    @Override
    @Transactional(readOnly = false)
    public ExpenseRes registerExpense(ExpenseReq expenseReq, String userAccount) {
        Member member = userRepository.findByAccount(userAccount)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 날짜
        LocalDate now = LocalDate.now();

        // 해당 달의 첫째 날 구하기
        LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        Specification<Budget> spec = BudgetSpecification.hasCondition(true,member.getMemberId(), null,null,null,firstDayOfMonth,lastDayOfMonth);
        List<Budget> budgetList = budgetRepository.findAll(spec);

        if(budgetList.isEmpty()){
            throw new CustomException(ErrorCode.BUDGET_DO_NOT_FOUND);
        }

        HashMap<String, Budget> budgetMap = budgetRepository.findAll(spec).stream()
            .collect(Collectors.toMap(
                e -> e.getCategory().getCategoryCode(), // 키 설정
                e -> e, // 값 설정
                (existing, replacement) -> existing, // 중복 키 처리 방법 (기존 값 유지)
                HashMap::new // 결과를 HashMap으로 수집
            ));


        AtomicInteger enrollCnt = new AtomicInteger();

        expenseReq.getBudgetList().stream().forEach(e -> {

            Expense expense = Expense.builder()
                .memo(e.getMemo())
                .creatDate(e.getCreateDate())
                .expense(e.getExpense())
                .member(member)
                .budget(budgetMap.get(e.getCategoryCode()))
                .category(budgetMap.get(e.getCategoryCode()).getCategory())
                .build();

            expenseRepository.save(expense);
            enrollCnt.set(+1);
        });

        return ExpenseRes.builder()
            .account(userAccount)
            .updateCnt(Integer.parseInt(String.valueOf(enrollCnt)))
            .build();
    }
}
