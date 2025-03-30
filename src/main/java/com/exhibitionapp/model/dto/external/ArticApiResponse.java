package com.exhibitionapp.model.dto.external;

import lombok.Data;

import java.util.List;

@Data
public class ArticApiResponse {

    private String preference;

    private Pagination pagination;

    private List<ArticArtwork> data;

    private Info info;

    private Config config;

    @Data
    public static class Config {
        private String iiif_url;
        private String website_url;
    }
}