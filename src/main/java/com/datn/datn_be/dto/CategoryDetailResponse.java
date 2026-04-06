package com.datn.datn_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponse {
    private Integer categoryId;
    private String name;
    private Integer parentId;
    private List<CategoryDetailResponse> subcategories;
}

