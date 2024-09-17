package com.budget.api.budget_api.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ExpenseReq {
    private List<ExpenseDetail> expenseList;

    @Getter
    @Setter
    public static class ExpenseDetail {
        @NotNull(message = "categoryCode is not null")
        private String categoryCode;
        @NotNull(message = "expense is not null")
        private Long expense;
        private String memo;
        @NotNull(message = "createDate is not null")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(example = "2024-09-14 11:10:36")
        private LocalDateTime createDate;  // 변경된 필드 타입
    }

}
