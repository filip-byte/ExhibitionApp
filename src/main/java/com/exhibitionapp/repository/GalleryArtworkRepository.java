package com.exhibitionapp.repository;

import com.exhibitionapp.model.entity.Gallery;
import com.exhibitionapp.model.entity.GalleryArtwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryArtworkRepository extends JpaRepository<GalleryArtwork, Long> {
    boolean existsByGalleryIdAndImageUrl(Long galleryId, String imageUrl);
    List<GalleryArtwork> findByGallery(Gallery gallery);
}