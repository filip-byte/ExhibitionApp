package com.exhibitionapp.service;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.entity.Gallery;

import java.util.List;

public interface ArtworkService {
    List<ArtworkDTO> getArtworks(String source, String query, int page, int limit);
    Gallery createGallery(String name, String description);
    void addArtworkToGallery(Long galleryId, String imageUrl);
    List<ArtworkDTO> getGalleryArtworks(Long galleryId);
}