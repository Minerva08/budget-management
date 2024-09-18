package com.budget.api.budget_api.global.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //사용자
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 입니다"),
    USER_REG_ERROR(INTERNAL_SERVER_ERROR, "서버 내부 오류로 사용자 등록에 실패했습니다."),

    //Budget
    BUDGET_DO_NOT_MOD(BAD_REQUEST,"해당 월의 예산이 아니므로 수정할 수 없습니다"),
    BUDGET_DO_NOT_FOUND(BAD_REQUEST,"사용자의 예산이 설정 되어 있지 않습니다"),

    //Expense
    EXPENSE_NOT_EXIST(BAD_REQUEST,"해당 사용자의 존재하지 않는 소비 입니다"),

    //Valid
    DATE_VALIDATE_PARAM(BAD_REQUEST, "날짜 유효성 조건에 맞지 않습니다."),
    VALIDATE_PARAM(BAD_REQUEST, "유효성 조건에 맞지 않습니다."),
    REQUIRED_PARAM(INTERNAL_SERVER_ERROR, "Not Null"),
    BUDGET_CATE_NOT_EXIST(BAD_REQUEST,"존재 하지 않는 에산 카테고리입니다"),
    BUDGET_NOT_EXIST(BAD_REQUEST,"존재 하지 않는 예산 입니다"),

    // 인증&인가
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "사용자 인증에 실패했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    AUTHORIZATION_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "Authorization 헤더값이 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰 인증 시간이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 형식이 유효하지 않습니다."),
    ACCESS_TOKEN_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "access 토큰 헤더값이 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "refresh 토큰을 찾을 수 없습니다."),

    //redis
    REDIS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 서버에서 오류가 발생했습니다.");



    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
