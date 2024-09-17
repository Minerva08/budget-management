package com.budget.api.budget_api.category.service;

import com.budget.api.budget_api.category.dto.CategoryRes;

public interface CategoryService {

    CategoryRes getCategoryList(String search);
}
