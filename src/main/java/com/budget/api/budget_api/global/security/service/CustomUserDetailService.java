package com.budget.api.budget_api.global.security.service;

import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.security.custom.CustomUserDetails;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userAccount) throws UsernameNotFoundException {
        // 로그인 요청 유저 조회
        Member loginUser = userRepository.findByAccount(userAccount).orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));
        // UserDetails에 담아서 return하면 AutneticationManager가 검증
        return new CustomUserDetails(loginUser);
    }
}
