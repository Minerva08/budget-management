package com.budget.api.budget_api.user.service;

import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.common.exception.CustomException;
import com.budget.api.budget_api.global.enums.GrantType;
import com.budget.api.budget_api.global.util.DateUtil;
import com.budget.api.budget_api.global.util.EncodeUtil;
import com.budget.api.budget_api.user.dto.join.JoinReq;
import com.budget.api.budget_api.user.dto.join.JoinRes;
import com.budget.api.budget_api.user.entity.Member;
import com.budget.api.budget_api.user.repo.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final EncodeUtil encodeUtil;

    @Override
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


}
