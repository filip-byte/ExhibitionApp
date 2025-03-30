package com.exhibitionapp.repository;

import com.exhibitionapp.model.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {

    List<Artwork> findByTitleContainingIgnoreCase(String title);
}