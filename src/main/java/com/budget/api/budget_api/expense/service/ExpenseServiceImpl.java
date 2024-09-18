package com.budget.api.budget_api.expense.service;

import com.budget.api.budget_api.budget.entity.Budget;
import com.budget.api.budget_api.budget.repo.BudgetRepository;
import com.budget.api.budget_api.budget.repo.BudgetSpecification;
import com.budget.api.budget_api.expense.dto.ExpenseModReq;
import com.budget.api.budget_api.expense.dto.ExpenseModReq.ExpenseMod;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import com.budget.api.budget_api.expense.entity.Expense;
import com.budget.api.budget_api.expense.repo.ExpenseRepository;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

        expenseReq.getExpenseList().stream().forEach(e -> {

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

    @Override
    @Transactional
    public ExpenseRes updateExpense(ExpenseModReq expenseModReq, String userAccount) {

        AtomicInteger updateCnt = new AtomicInteger(0);


        List<Budget> budgetsByMember = budgetRepository.findByAccount(userRepository.findByAccount(userAccount).get().getMemberId());

        List<Long> modExpenseIds = expenseModReq.getModList().stream().map(ExpenseMod::getExpenseId)
            .toList();


        HashMap<Long, Expense> expenseMap = expenseRepository.findByUserBudget(modExpenseIds, userAccount).stream()
            .collect(Collectors.toMap(
                Expense::getExpenseId, // 키 설정
                e -> e, // 값 설정
                (existing, replacement) -> existing, // 중복 키 처리 방법 (기존 값 유지)
                HashMap::new // 결과를 HashMap으로 수집
            ));


        if(modExpenseIds.size()!=expenseMap.size()){
            throw new CustomException(ErrorCode.EXPENSE_NOT_EXIST);
        }

        expenseModReq.getModList().stream().forEach(ex->{

            Expense orgExpenseInfo = expenseMap.get(ex.getExpenseId());

            Budget budgetInfo = orgExpenseInfo.getBudget();

            if(ex.getModeCategoryCode()!=null){
                 budgetInfo = findMatchingBudget(budgetsByMember, ex).orElseThrow(
                    () -> new CustomException(ErrorCode.BUDGET_CATE_NOT_EXIST));

            }

            Expense modExpenseInfo = Expense.builder()
                .expenseId(ex.getExpenseId())
                .expense(ex.getModExpense()==null? orgExpenseInfo.getExpense():ex.getModExpense())
                .member(orgExpenseInfo.getMember())
                .memo(ex.getMemo()==null?orgExpenseInfo.getMemo():ex.getMemo())
                .budget(budgetInfo)
                .category(budgetInfo.getCategory())
                .build();

            expenseRepository.save(modExpenseInfo);
            updateCnt.set(+1);
        });

        return ExpenseRes.builder()
            .account(userAccount)
            .updateCnt(Integer.parseInt(String.valueOf(updateCnt)))
            .build();
    }

    public Optional<Budget> findMatchingBudget(List<Budget> budgetsByMember, ExpenseMod mod) {
        if (mod.getModeCategoryCode() != null) {
            return budgetsByMember.stream()
                .filter(budget -> budget.getCategory().getCategoryCode().equals(mod.getModeCategoryCode()))
                .filter(budget -> budget.getStartDate().isBefore(LocalDate.now()))
                .filter(budget -> budget.getEndDate().isAfter(LocalDate.now()))
                .findFirst();
        }
        return Optional.empty(); // `modeCategoryCode`가 null인 경우 빈 Optional 반환
    }
}
