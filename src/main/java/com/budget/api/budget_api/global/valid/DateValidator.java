package com.budget.api.budget_api.global.valid;

import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateValidator {

    /**
     * 통계 요청 날짜 관련 유효성 검증 메서드입니다.
     *
     * @param type date, hour
     * @param startDateTime 통계 시작 일자
     * @param endDateTime 통계 종료 일자
     *
     * */
    public static void validateDate(List<String> type, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("[{}] Validate by Date- type:{}, startDateTime : {}, endDateTime:{}",Thread.currentThread().getStackTrace()[1].getMethodName(),type.stream().toArray(),startDateTime,endDateTime);

        if(startDateTime.isAfter(endDateTime))
            throw new CustomException(ErrorCode.DATE_VALIDATE_PARAM);

        if(startDateTime.isAfter(LocalDateTime.now()))
            throw new CustomException(ErrorCode.DATE_VALIDATE_PARAM);

        if(endDateTime.isAfter(LocalDateTime.now()))
            throw new CustomException(ErrorCode.DATE_VALIDATE_PARAM);

        if (type.size() > 1) {
            if(Duration.between(startDateTime, endDateTime).toDays() > 30)
                throw new CustomException(ErrorCode.DATE_VALIDATE_PARAM);

        }
    }
}

