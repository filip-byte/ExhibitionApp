package com.exhibitionapp.service;

import com.exhibitionapp.model.dto.ArtworkDTO;

import java.util.List;

public interface ArtworkService {
    List<ArtworkDTO> getArtworks(int page, int limit, String query);
}