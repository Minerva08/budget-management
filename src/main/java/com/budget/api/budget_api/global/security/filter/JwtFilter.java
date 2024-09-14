package com.budget.api.budget_api.global.security.filter;

import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.enums.GrantType;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import com.budget.api.budget_api.global.security.exception.JwtAuthenticationException;
import com.budget.api.budget_api.global.security.token.TokenManager;
import com.budget.api.budget_api.user.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        try {
            // Authorization 헤더 검증
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.info("Authorization header 정보 없음");
                throw new JwtAuthenticationException(ErrorCode.AUTHORIZATION_HEADER_MISSING);
            }

            //Bearer 부분 제거 후 순수 토큰만 획득
            String accessToken = authorization.substring(7);
            if(accessToken.contains(" ")){
                log.error("trim AccessToken :{}",accessToken);
            }

            //토큰 유효성 검증
            tokenManager.validateToken(accessToken);

            //토큰에서 category와 username 획득
            if (!tokenManager.isAccessToken(accessToken)){
                throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN);
            }

            String username = tokenManager.getUsername(accessToken);
            String userAccount = tokenManager.getUserAccount(accessToken);
            String userGrant = tokenManager.getGrant(accessToken);

            //userEntity를 생성하여 값 set
            Member user = Member.builder()
                .account(userAccount)
                .pw("temp")
                .grant(GrantType.valueOf(userGrant))
                .build();

            //UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null,
                customUserDetails.getAuthorities());

            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            log.info("JWT 인증 실패: {}", e.getMessage());
            handleException(request, response, e, filterChain);
        } catch (Exception e) {
            log.info("기타 인증 오류 발생: {}", e.getMessage());
            handleException(request, response, new JwtAuthenticationException("기타 인증 에러 발생", e), filterChain);
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, JwtAuthenticationException e, FilterChain filterChain) throws IOException, ServletException {
        request.setAttribute("exception", e);
        filterChain.doFilter(request, response);
    }
}
