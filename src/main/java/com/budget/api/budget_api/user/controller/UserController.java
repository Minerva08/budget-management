package com.budget.api.budget_api.user.controller;

import com.budget.api.budget_api.user.dto.join.JoinReq;
import com.budget.api.budget_api.user.dto.join.JoinRes;
import com.budget.api.budget_api.user.service.UserService;
import com.budget.api.budget_api.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private UserService userService;

    /**
     * 사용자 회원가입
     *
     * @param joinReqDto 회원가입 요청 DTO
     * @return 사용자 Id
     * @throws com.budget.api.budget_api.global.common.exception.CustomException 계정명 중복 -> ACCOUNT_ALREADY_REGISTERED
     */
    @Operation(summary = "사용자 회원가입", description = "사용자는 계정과 비밀번호로 회원 가입합니다.")
    @PostMapping("/join")
    public ResponseEntity<CommonResponse<JoinRes>> signUp(
        @RequestBody @Valid JoinReq joinReqDto) {
        CommonResponse<JoinRes> response = CommonResponse.ok("회원가입에 성공하였습니다.",
            userService.signUp(joinReqDto));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
