package com.budget.api.budget_api.global.security.filter;

import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.error.ErrorResponse;
import com.budget.api.budget_api.global.enums.AuthStatus;
import com.budget.api.budget_api.global.enums.GrantType;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import com.budget.api.budget_api.global.security.token.TokenManager;
import com.budget.api.budget_api.user.dto.login.LoginReq;
import com.budget.api.budget_api.user.dto.login.LoginRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE; // JSON 타입의 데이터로 오는 로그인 요청만 처리
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    public LoginFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager,
        TokenManager tokenManager) {
        super(new AntPathRequestMatcher("/users/login", HttpMethod.POST.name())); // 위에서 설정한 "login" + POST로 온 요청을 처리하기 위해 설정
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException, IOException {
        if (!Objects.equals(request.getContentType(), CONTENT_TYPE)) {
            throw new AuthenticationServiceException(
                "지원하지 않는 Content-Type 입니다: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(),
            StandardCharsets.UTF_8);
        LoginReq loginRequestDto = objectMapper.readValue(messageBody,
            LoginReq.class);

        Set<ConstraintViolation<LoginReq>> violations = Validation.buildDefaultValidatorFactory()
            .getValidator().validate(loginRequestDto);

        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new AuthenticationServiceException("Validation failed: " + errorMessages);
        }

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getAccount(), loginRequestDto.getPw());
        return authenticationManager.authenticate(authToken); // AuthenticationManager에 토큰 전달
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = customUserDetails.getUsername();
        GrantType grant = customUserDetails.getGrant();
        String userAccount = customUserDetails.getUserAccount();
        tokenManager.issueTokens(response, userAccount,username,grant);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.error("사용자 로그인 인증 실패: {}", failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
            objectMapper.writeValueAsString(new ErrorResponse(ErrorCode.AUTHENTICATION_FAILED)));
    }
}
