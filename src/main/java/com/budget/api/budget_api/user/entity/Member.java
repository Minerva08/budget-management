package com.budget.api.budget_api.user.entity;

import com.budget.api.budget_api.global.enums.GrantType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(unique = true) // 고유 제약 조건 설정
    private String account;
    private String email;
    private String username;
    private String pw;
    private LocalDate birth;
    @Column(name = "grant")
    @Enumerated(EnumType.STRING)
    private GrantType grant;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

}
