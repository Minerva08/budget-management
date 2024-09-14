package com.budget.api.budget_api.user.dto.login;

import com.budget.api.budget_api.global.enums.AuthStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRes {
    private String account;
    private String grant;
    private AuthStatus authStatus;
}
