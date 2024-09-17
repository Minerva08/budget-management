package com.budget.api.budget_api.budget.controller;

import com.budget.api.budget_api.budget.dto.BudgetListRes;
import com.budget.api.budget_api.budget.dto.BudgetModReq;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            budgetService.registerBudgetByUser(budgeInfo,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    /**
     * 사용자 예산 목록
     *
     *
     * @return 사용자 account, 등록된 예산 목록
     * @param startDate, 조회 시작 일자
     * @param endDate, 조회 종료 일자
     * @param budgetMin, 예산 최소 금액
     * @param budgetMax, 예산 최대 금액
     * @param category, 조회 예산 카테고리(code,name)
     * @throws com.budget.api.budget_api.global.common.exception.CustomException, CustomException
     */
    @Operation(summary = "사용자 예산 조회", description = "사용자가 등록한 예산을 조회 한다.")
    @GetMapping
    public ResponseEntity<CommonResponse<BudgetListRes>> getBudgets(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) Long budgetMin,
        @RequestParam(required = false) Long budgetMax,
        @RequestParam(required = false) String category,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<BudgetListRes> response = CommonResponse.ok("예산 목록이 조회되었습니다",
            budgetService.getBudgetList(startDate,endDate,budgetMin,budgetMax,category,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    /**
     * 사용자 예산 수정
     *
     * @param modReq 예산 변경 요청 DTO
     * @return 사용자 account, 등록된 예산 개수
     * @throws com.budget.api.budget_api.global.common.exception.CustomException
     */
    @Operation(summary = "사용자 예산 수정", description = "사용자는 등록한 얘산을 수정 한다.(현재 달의 예산만 수정 가능하다).")
    @PutMapping
    public ResponseEntity<CommonResponse<BudgetRes>> modBudgets(
        @RequestBody BudgetModReq modReq,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommonResponse<BudgetRes> response = CommonResponse.ok("예산이 변경 되었습니다",
            budgetService.updateBudget(modReq,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
