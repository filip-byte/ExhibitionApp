package com.exhibitionapp.service;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.GalleryDTO;
import java.util.List;

public interface ArtworkService {
    List<ArtworkDTO> getArtworks(int page, int limit, String query, String source);
    GalleryDTO createGallery(String name, String description);
}