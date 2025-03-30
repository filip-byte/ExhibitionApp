package com.exhibitionapp.model.dto.external;

import lombok.Data;

@Data
public class Pagination {

    private int total;

    private int limit;

    private int offset;

    private int total_pages;

    private int current_page;
}