package com.budget.api.budget_api.user.repo;

import com.budget.api.budget_api.user.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByAccount(String account);
}
