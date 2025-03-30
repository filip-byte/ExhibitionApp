package com.exhibitionapp.model.dto.external;

import lombok.Data;

import java.util.List;

@Data
public class MetSearchResponse {
    private int total;
    private List<Integer> objectIDs;
}