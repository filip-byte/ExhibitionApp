package com.exhibitionapp.controller;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.GalleryDTO;
import com.exhibitionapp.model.entity.Gallery;
import com.exhibitionapp.service.ArtworkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getArtworks(
            @RequestParam String source,
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit) {
        List<ArtworkDTO> artworks = artworkService.getArtworks(source, q, page, limit);
        return ResponseEntity.ok(artworks);
    }

    @PostMapping("/galleries")
    public ResponseEntity<GalleryDTO> createGallery(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        Gallery gallery = artworkService.createGallery(name, description);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GalleryDTO(gallery.getId(), gallery.getName(), gallery.getDescription()));
    }

    @PostMapping("/galleries/{id}/artworks")
    public ResponseEntity<Void> addArtworkToGallery(
            @PathVariable Long id,
            @RequestParam String imageUrl) {
        artworkService.addArtworkToGallery(id, imageUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/galleries/{id}")
    public ResponseEntity<List<ArtworkDTO>> getGalleryArtworks(@PathVariable Long id) {
        List<ArtworkDTO> artworks = artworkService.getGalleryArtworks(id);
        return ResponseEntity.ok(artworks);
    }
}