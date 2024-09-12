package com.budget.api.budget_api.global.security.service;

import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.security.token.TokenManager;
import com.budget.api.budget_api.global.util.EncodeUtil;
import com.budget.api.budget_api.user.dto.JoinReq;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EncodeUtil encodeUtil;
    private final TokenManager tokenManager;

    public long signUp(JoinReq joinReqDto) {
        if (userRepository.existsByAccount(joinReqDto.getAccount())) {
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_REGISTERED);
        }

        return userRepository.save(
            Member.builder()
                .account(joinReqDto.getAccount())
                .pw(encodeUtil.encodePassword(joinReqDto.getPw()))
                .build()
        ).getId();
    }

    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        //get refresh token
        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("refresh")).findFirst().orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)).getValue();

        tokenManager.validateToken(refreshToken);

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        if (!tokenManager.isRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        tokenManager.validateRefreshToken(refreshToken);
        String userAccount = tokenManager.getUserAccount(refreshToken);
        tokenManager.deleteRefreshToken(refreshToken);
        tokenManager.issueTokens(response,userAccount ,null);
    }
}
