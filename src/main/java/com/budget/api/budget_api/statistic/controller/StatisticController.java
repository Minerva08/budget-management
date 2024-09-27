package com.budget.api.budget_api.statistic.controller;

import com.budget.api.budget_api.global.common.CommonResponse;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import com.budget.api.budget_api.statistic.dto.StatisticRes;
import com.budget.api.budget_api.statistic.enums.StatisticStandard;
import com.budget.api.budget_api.statistic.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    /**
     * 사용자 지출 통계
     *
     * @param  nowDate, 오늘 날짜
     * @param subStandard, 카테고리
     * @return 사용자 account, 등록된 예산 개수
     * @throws com.budget.api.budget_api.global.common.exception.CustomException
     */
    @Operation(summary = "지출 통계", description = "월별/사용자별/요일별 지출 통계")
    @GetMapping("/{standard}")
    public ResponseEntity<CommonResponse<StatisticRes>> getExpenseStatistic(
        @PathVariable(value = "standard") StatisticStandard standard,
        @RequestParam(value = "nowDate") String nowDate,
        @RequestParam(required = false, value = "subStandard") String subStandard,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonResponse<StatisticRes> response = CommonResponse.ok("지출 통계에 성공 하였습니다",
            statisticService.getStatistic(standard,subStandard,nowDate,userDetails.getUserAccount()));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
