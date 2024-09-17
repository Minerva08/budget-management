package com.budget.api.budget_api.category.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryRes {
    private int cnt;
    private List<Cate> cateList;

    @Getter
    @Builder
    public static class Cate {
        private String code;
        private String name;
    }
    @Builder
    public CategoryRes(List<Cate> cateList) {
        this.cnt = cateList.size();
        this.cateList = cateList;
    }
}
