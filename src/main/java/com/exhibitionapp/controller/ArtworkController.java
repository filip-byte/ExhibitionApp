package com.exhibitionapp.controller;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.GalleryDTO;
import com.exhibitionapp.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    @Autowired
    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getArtworks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "dogs") String q,
            @RequestParam(defaultValue = "Artic") String source) {
        try {
            List<ArtworkDTO> artworks = artworkService.getArtworks(page, limit, q, source);
            return new ResponseEntity<>(artworks, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PostMapping("/galleries")
    public ResponseEntity<GalleryDTO> createGallery(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        GalleryDTO gallery = artworkService.createGallery(name, description);
        return new ResponseEntity<>(gallery, HttpStatus.CREATED);
    }

    @PostMapping("/galleries/{galleryId}/artworks")
    public ResponseEntity<Void> addArtworkToGallery(
            @PathVariable Long galleryId,
            @RequestParam String imageUrl) {
        artworkService.addArtworkToGallery(galleryId, imageUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}