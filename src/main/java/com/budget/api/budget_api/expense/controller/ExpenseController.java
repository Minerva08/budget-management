package com.budget.api.budget_api.expense.controller;

import com.budget.api.budget_api.expense.dto.ExpenseDetailOneRes;
import com.budget.api.budget_api.expense.dto.ExpenseModReq;
import com.budget.api.budget_api.expense.dto.ExpenseReq;
import com.budget.api.budget_api.expense.dto.ExpenseRes;
import com.budget.api.budget_api.expense.dto.ExpenseSearchRes;
import com.budget.api.budget_api.expense.service.ExpenseService;
import com.budget.api.budget_api.global.common.CommonResponse;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * 사용자 지출 수정
     *
     * @param expenseModReq 지출 등록 요청 DTO
     * @return 사용자 account, 등록된 지출 개수
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


    /**
     * 사용자 지출 조회(목록)
     *
     * @param costMin, 최소 비용
     * @param costMax, 최대 비용
     * @param startDate, 지출 조회 시작 기간(필수)
     * @param endDate, 지출 조회 종료 기간(필수)
     * @param categoryCode, 지출 조회 카테고리 Code
     * @return 사용자 account, 검색 조건의 해당 지출 목록
     * @throws com.budget.api.budget_api.global.common.exception.CustomException, CustomException
     */
    @Operation(summary = "사용자 지출 조회(목록)", description = "사용자의 지출을 조회(목록) 한다.")
    @GetMapping("list")
    public ResponseEntity<CommonResponse<ExpenseSearchRes>> modExpense(
        @RequestParam(required = false)
         Long costMin,
        @RequestParam(required = false)
         Long costMax,
        @RequestParam
         LocalDate startDate,
        @RequestParam
         LocalDate endDate,
        @RequestParam(required = false)
         String categoryCode,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<ExpenseSearchRes> response = CommonResponse.ok("지출 정보 조회에 성공 하였습니다",
            expenseService.getExpenseList(costMin,costMax,startDate,endDate,categoryCode,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    /**
     * 사용자 지출 상세
     *
     * @pathVariable expenseId, 지출 id
     * @return 사용자 account, 변경된 지출 개수
     * @throws com.budget.api.budget_api.global.common.exception.CustomException, CustomException
     */
    @Operation(summary = "사용자 지출 조회(상세)", description = "사용자의 지출을 단건 조회 한다.")
    @GetMapping("/{expenseId}")
    public ResponseEntity<CommonResponse<ExpenseDetailOneRes>> getDetail(
        @PathVariable Long expenseId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<ExpenseDetailOneRes> response = CommonResponse.ok("지출 정보 단건 조회에 성공 하였습니다",
            expenseService.getExpenseDetail(expenseId,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
