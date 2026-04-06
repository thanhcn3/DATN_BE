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
public class PaginationResponse<T> {
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<T> content;
    private boolean hasNext;
    private boolean hasPrevious;
}

