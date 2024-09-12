package com.budget.api.budget_api.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginReq {
    private String email;
    private String account;
    private String username;
    private String pw;
    private String birth;
    private String grant;
    private String createDate;

}
