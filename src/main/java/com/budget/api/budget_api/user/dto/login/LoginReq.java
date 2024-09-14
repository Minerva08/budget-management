package com.budget.api.budget_api.user.dto.login;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginReq {
    private String email;
    private String account;
    private String pw;
    private String birth;
    private String grant;

}
