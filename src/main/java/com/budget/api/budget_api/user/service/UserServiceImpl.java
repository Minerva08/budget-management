package com.budget.api.budget_api.user.service;

import ch.qos.logback.core.spi.ErrorCodes;
import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.enums.AuthStatus;
import com.budget.api.budget_api.global.enums.GrantType;
import com.budget.api.budget_api.global.security.token.JwtTokenProvider;
import com.budget.api.budget_api.global.security.token.TokenManager;
import com.budget.api.budget_api.global.util.DateUtil;
import com.budget.api.budget_api.global.util.EncodeUtil;
import com.budget.api.budget_api.user.dto.join.JoinReq;
import com.budget.api.budget_api.user.dto.join.JoinRes;
import com.budget.api.budget_api.user.dto.login.LoginRes;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final EncodeUtil encodeUtil;
    private final TokenManager tokenManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = false)
    public JoinRes signUp(JoinReq joinReqDto) {

        try {
            LocalDate birthDay = DateUtil.convertStringToDate(joinReqDto.getBirth());

            String password = encodeUtil.encodePassword(joinReqDto.getPw());

            LocalDateTime now = LocalDateTime.now();

            Member joinMember = Member.builder()
                .account(joinReqDto.getAccount())
                .email(joinReqDto.getEmail())
                .grant(GrantType.valueOf(joinReqDto.getGrant()))
                .birth(birthDay)
                .pw(password)
                .username(joinReqDto.getUsername())
                .createDate(now)
                .updateDate(now)
                .build();

            Member saveMember = userRepository.save(joinMember);
            return JoinRes.builder().account(saveMember.getAccount()).build();
        }catch (DuplicateKeyException e){
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }

    }

    @Override
    public LoginRes jwtReIssue(String auth, String account) {
        String token = auth.substring(7);

        if(!tokenManager.isRefreshToken(token)){
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        Member memberInfo = userRepository.findByAccount(account)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!account.equals(tokenManager.getUserAccount(token))){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        tokenManager.validateRefreshToken(token);

        String access = jwtTokenProvider.createJwt("access", account, memberInfo.getUsername(),
            memberInfo.getGrant());
        String refresh = jwtTokenProvider.createJwt("refresh",account,null,null);

        return LoginRes.builder()
            .account(account)
            .refreshToken(refresh)
            .accessToken(access)
            .authStatus(AuthStatus.PERMIT)
            .grant(String.valueOf(memberInfo.getGrant()))
            .build();
    }


}
