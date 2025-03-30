package com.exhibitionapp.repository;

import com.exhibitionapp.model.entity.GalleryArtwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryArtworkRepository extends JpaRepository<GalleryArtwork, Long> {
}