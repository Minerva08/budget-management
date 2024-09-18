package com.budget.api.budget_api.expense.controller;

import com.budget.api.budget_api.expense.dto.ExpenseModReq;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import com.budget.api.budget_api.expense.service.ExpenseService;
import com.budget.api.budget_api.global.common.CommonResponse;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * 사용자 지출 등록
     *
     * @param expenseReq 지출 등록 요청 DTO
     * @return 사용자 account, 등록된 예산 개수
     * @throws com.budget.api.budget_api.global.common.exception.CustomException
     */
    @Operation(summary = "사용자 지출등록", description = "사용자의 카테고리별 지출을 등록한다.")
    @PostMapping
    public ResponseEntity<CommonResponse<ExpenseRes>> registerExpense(
        @RequestBody @Valid ExpenseReq expenseReq,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<ExpenseRes> response = CommonResponse.ok("지출 등록에 성공 하였습니다",
            expenseService.registerExpense(expenseReq,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }


    /**
     * 사용자 지출 등록
     *
     * @param expenseModReq 지출 등록 요청 DTO
     * @return 사용자 account, 변경된 지출 개수
     * @throws com.budget.api.budget_api.global.common.exception.CustomException, CustomException
     */
    @Operation(summary = "사용자 지출 변경", description = "사용자의 카테고리별 지출을 변경 한다.")
    @PutMapping
    public ResponseEntity<CommonResponse<ExpenseRes>> modExpense(
        @RequestBody @Valid ExpenseModReq expenseModReq,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<ExpenseRes> response = CommonResponse.ok("지출 정보 변경에 성공 하였습니다",
            expenseService.updateExpense(expenseModReq,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
