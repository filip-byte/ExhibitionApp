package com.exhibitionapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkDTO implements Serializable {

    private int id;

    private String title;

    private String imageUrl;
}