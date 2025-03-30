package com.exhibitionapp.model.dto.external;

import lombok.Data;

import java.util.List;

@Data
public class Info {

    private String license_text;

    private List<String> license_links;

    private String version;
}