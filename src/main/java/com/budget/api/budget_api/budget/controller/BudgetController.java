package com.budget.api.budget_api.budget.controller;

import com.budget.api.budget_api.budget.dto.BudgetReq;
import com.budget.api.budget_api.budget.dto.BudgetRes;
import com.budget.api.budget_api.budget.service.BudgetService;
import com.budget.api.budget_api.global.common.CommonResponse;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * 사용자 예산 등록
     *
     * @param budgeInfo 예산 등록 요청 DTO
     * @return 사용자 account, 등록된 예산 개수
     * @throws com.budget.api.budget_api.global.common.exception.CustomException
     */
    @Operation(summary = "사용자 예산등록", description = "사용자는 카테고리별 예산을 등록한다.")
    @PostMapping
    public ResponseEntity<CommonResponse<BudgetRes>> enrolledBudget(
        @RequestBody @Valid BudgetReq budgeInfo,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<BudgetRes> response = CommonResponse.ok("예산 등록에 성공 하였습니다",
            budgetService.registerBudgetByUser(budgeInfo,userDetails.getUserAccount(),userDetails.getUsername()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }



}
