package com.budget.api.budget_api.budget.repo;

import com.budget.api.budget_api.budget.entity.Budget;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long>,
    JpaSpecificationExecutor<Budget> {

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT b FROM Budget b WHERE b.budgetId IN :ids")
    List<Budget> findByIdWithCategory(@Param("ids") List<Long> ids);


    @EntityGraph(attributePaths = {"category", "member"})
    @Query("SELECT b FROM Budget b WHERE b.member.memberId = :memberId")
    List<Budget> findByAccount(
        @Param("memberId") Long memberId
    );
}
