package com.budget.api.budget_api.budget.service;

import com.budget.api.budget_api.budget.dto.BudgetReq;
import com.budget.api.budget_api.budget.dto.BudgetRes;
import com.budget.api.budget_api.budget.entity.Budget;
import com.budget.api.budget_api.budget.repo.BudgetRepository;
import com.budget.api.budget_api.category.entity.Category;
import com.budget.api.budget_api.category.repo.CategoryRepository;
import com.budget.api.budget_api.category.repo.CategorySpecification;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.util.DateUtil;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService{

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BudgetRes registerBudgetByUser(BudgetReq budgeInfo, String userAccount,
        String username) {

        LocalDate startLocalDate = DateUtil.convertStringToDate(budgeInfo.getStartDate());
        LocalDate endLocalDate = DateUtil.convertStringToDate(budgeInfo.getEndDate());

        Member memberInfo = userRepository.findByAccount(userAccount)
            .orElseThrow(() -> new CustomException(
                ErrorCode.USER_NOT_FOUND));


        Specification<Category> spec = CategorySpecification.hasNameOrCode(null);

        List<Category> list = categoryRepository.findAll(spec);

        AtomicInteger updateCnt = new AtomicInteger();

        budgeInfo.getBudgetList().forEach(e->{

            Budget budget = Budget.builder()
                .budget(e.getBudget())
                .startDate(startLocalDate)
                .endDate(endLocalDate)
                .member(memberInfo)
                .category(
                    list.stream().filter(cate -> cate.getCategoryCode().equals(e.getCategoryCode()))
                        .findFirst()
                        .orElseThrow(() -> new CustomException(ErrorCode.BUDGET_CATE_NOT_EXIST))
                )
                .build();

            budgetRepository.save(budget);
            updateCnt.addAndGet(1);
        });

        return BudgetRes.builder()
            .account(userAccount)
            .updateCnt(Integer.valueOf(String.valueOf(updateCnt)))
            .build();
    }
}
