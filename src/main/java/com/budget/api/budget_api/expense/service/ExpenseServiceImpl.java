package com.budget.api.budget_api.expense.service;

import com.budget.api.budget_api.budget.entity.Budget;
import com.budget.api.budget_api.budget.repo.BudgetRepository;
import com.budget.api.budget_api.budget.repo.BudgetSpecification;
import com.budget.api.budget_api.category.entity.Category;
import com.budget.api.budget_api.category.repo.CategoryRepository;
import com.budget.api.budget_api.expense.dto.ExpenseDetail;
import com.budget.api.budget_api.expense.dto.ExpenseDetailOneRes;
import com.budget.api.budget_api.expense.dto.ExpenseModReq;
import com.budget.api.budget_api.expense.dto.ExpenseModReq.ExpenseMod;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import com.budget.api.budget_api.expense.dto.ExpenseSearch;
import com.budget.api.budget_api.expense.dto.ExpenseSearchRes;
import com.budget.api.budget_api.expense.dto.ExpenseSearchRes.SumExpense;
import com.budget.api.budget_api.expense.entity.Expense;
import com.budget.api.budget_api.expense.repo.ExpenseRepository;
import com.budget.api.budget_api.expense.repo.ExpenseSpecification;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
    private final CategoryRepository categoryRepository;


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
                .creatDate(e.getCreatedTime())
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
                .excludingTotal(ex.getExcludingTotal()==null ? orgExpenseInfo.getExcludingTotal() : ex.getExcludingTotal() )
                .creatDate(LocalDateTime.now())
                .build();

            expenseRepository.save(modExpenseInfo);
            updateCnt.set(+1);
        });

        return ExpenseRes.builder()
            .account(userAccount)
            .updateCnt(Integer.parseInt(String.valueOf(updateCnt)))
            .build();
    }

    @Override
    public ExpenseSearchRes getExpenseList(Long costMin, Long costMax, LocalDate startDate,
        LocalDate endDate, String categoryCode, String userAccount) {

        Category categoryInfo = new Category();

        if(categoryCode!=null){
            categoryInfo = categoryRepository.findByCategoryCode(categoryCode)
                .orElseThrow(() -> new CustomException(ErrorCode.BUDGET_CATE_NOT_EXIST));

        }

        ExpenseSearch search = ExpenseSearch.builder()
            .costMin(costMin)
            .costMax(costMax)
            .categoryId(categoryInfo.getCategoryId())
            .startDate(startDate)
            .endDate(endDate)
            .userAccount(userAccount)
            .build();

        Specification<Expense> spec = ExpenseSpecification.hasCondition(search);

        List<Expense> searchList= expenseRepository.findAll(spec);

        LocalDate startBySearch = startDate;
        LocalDate endBySearch = endDate;

        if(startDate==null){
            Optional<Expense> firstExpense = searchList.stream()
                .sorted(Comparator.comparing(Expense::getCreatDate)) // createDate 오름차순 정렬
                .findFirst();

            startBySearch = LocalDate.from(firstExpense.get().getCreatDate());
        }

        if(endDate==null){
            Optional<Expense> firstExpense = searchList.stream()
                .sorted(Comparator.comparing(Expense::getCreatDate).reversed()) // createDate 내림차순 정렬
                .findFirst();

            endBySearch = LocalDate.from(firstExpense.get().getCreatDate());

        }

        Map<Long, List<Expense>> groupedByCategory = getGroupedByCategory(searchList);

        AtomicReference<Long> totalExpenseExcludeExpense = new AtomicReference<>(0l);

        // Step 3: 그룹화된 데이터를 Stream으로 변환해 SumExpense 객체로 변환
        List<SumExpense> sumExpenses = groupedByCategory.entrySet().stream()
            .map(entry -> {
                List<Expense> expensesForCategory = entry.getValue();

                // categoryName은 첫 번째 항목의 카테고리 이름을 사용
                String name = expensesForCategory.get(0).getCategory().getCategoryName();
                String code = expensesForCategory.get(0).getCategory().getCategoryCode();

                List<ExpenseDetail> expenseDetails = expensesForCategory.stream().map(expense -> {
                    return ExpenseDetail.builder()
                        .createdTime(expense.getCreatDate())
                        .expense(expense.getExpense())
                        .memo(expense.getMemo())
                        .isExcludingTotal(expense.getExcludingTotal())
                        .build();
                }).collect(Collectors.toList());

                // 카테고리별 비용 합계 계산
                long totalByCategory = expensesForCategory.stream()
                    .filter(Expense::getExcludingTotal)
                    .mapToLong(Expense::getExpense)
                    .sum();

                totalExpenseExcludeExpense.updateAndGet(v -> v + expensesForCategory.stream()
                    .filter(expense -> !expense.getExcludingTotal())  // false인 항목 필터링
                    .mapToLong(Expense::getExpense)  // 비용을 long으로 매핑
                    .sum());

                // SumExpense 객체 생성
                SumExpense sumExpense = new SumExpense();
                sumExpense.setCategoryCode(code);
                sumExpense.setCategoryName(name);
                sumExpense.setExpenseList(expenseDetails);
                sumExpense.setTotalByCategory(totalByCategory);

                return sumExpense;
            }).collect(Collectors.toList());


        // Step 4: ExpenseSearchRes에 sumExpenses를 설정하여 반환
        return ExpenseSearchRes
            .builder()
            .expenseCnt(sumExpenses.size())
            .account(userAccount)
            .startDate(startBySearch)
            .endDate(endBySearch)
            .sumExpense(sumExpenses)
            .totalExpense(Long.parseLong(String.valueOf(totalExpenseExcludeExpense)))
            .build();
    }

    private Map<Long, List<Expense>> getGroupedByCategory(List<Expense> searchList) {
        // Step 2: Expense를 categoryId (카테고리) 기준으로 그룹화
        Map<Long, List<Expense>> groupedByCategory = searchList.stream()
            .collect(Collectors.groupingBy(expense -> expense.getCategory().getCategoryId()));
        return groupedByCategory;
    }

    @Override
    public ExpenseDetailOneRes getExpenseDetail(Long expenseId, String userAccount) {

        ExpenseSearch search = ExpenseSearch.builder()
            .userAccount(userAccount)
            .expenseId(expenseId)
            .build();


        Specification<Expense> spec = ExpenseSpecification.hasCondition(search);

        Expense expenseOne = expenseRepository.findAll(spec).stream().findFirst().get();

        return ExpenseDetailOneRes.builder()
            .categoryCode(expenseOne.getCategory().getCategoryCode())
            .categoryName(expenseOne.getCategory().getCategoryName())
            .account(userAccount)
            .expense(expenseOne.getExpense())
            .createdTime(expenseOne.getCreatDate())
            .memo(expenseOne.getMemo())
            .isExcludingTotal(expenseOne.getExcludingTotal())
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
        return Optional.empty();
    }
}
