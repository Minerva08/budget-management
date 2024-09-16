package com.budget.api.budget_api.category.controller;

import com.budget.api.budget_api.category.dto.CategoryRes;
import com.budget.api.budget_api.category.service.CategoryService;
import com.budget.api.budget_api.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

     /**
     * @param search 카테고리 조회 파라미터
     * @return CategoryRes 카테고리 목록
     */
    @Operation(summary = "예산 카테고리 목록", description = "예산 등록시 사용 되는 카테고리 목록")
    @GetMapping
    public ResponseEntity<CommonResponse<CategoryRes>> getBudgetCategories(
        @RequestParam(value = "search",required = false) String search) {
        CommonResponse<CategoryRes> response = CommonResponse.ok("예산 카테고리가 조회 되었습니다",
            categoryService.getCategoryList(search));

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
