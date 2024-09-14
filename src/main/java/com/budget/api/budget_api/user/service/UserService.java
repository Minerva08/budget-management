package com.budget.api.budget_api.user.service;

import com.budget.api.budget_api.user.dto.join.JoinReq;
import com.budget.api.budget_api.user.dto.join.JoinRes;
import jakarta.validation.Valid;

public interface UserService {

    JoinRes signUp(@Valid JoinReq joinReqDto);

}
