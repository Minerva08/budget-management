package com.budget.api.budget_api.category.service;

import com.budget.api.budget_api.category.repo.CategoryRepository;
import com.budget.api.budget_api.category.dto.CategoryRes;
import com.budget.api.budget_api.category.dto.CategoryRes.Cate;
import com.budget.api.budget_api.category.entity.Category;
import com.budget.api.budget_api.category.repo.CategorySpecification;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryRes getCategoryList(String search) {

        Specification<Category> spec = CategorySpecification.hasNameOrCode(search);

        List<Category> list = categoryRepository.findAll(spec);

        if(!list.isEmpty()){

            List<Cate> cateList = list.stream()
                .map(e ->
                    Cate.builder()
                        .name(e.getCategoryName())
                        .code(e.getCategoryCode())
                        .build()
                ).toList();
            return CategoryRes.builder()
                .cateList(cateList)
                .build();
        }

        return CategoryRes.builder()
            .cateList(new ArrayList<>())
            .build();
    }
}
