package com.exhibitionapp.controller;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.service.ArtworkService;
import com.exhibitionapp.service.external.ArticApiException;
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
            @RequestParam(defaultValue = "dogs") String q) {
        try {
            List<ArtworkDTO> artworks = artworkService.getArtworks(page, limit, q);
            return new ResponseEntity<>(artworks, HttpStatus.OK);
        } catch (ArticApiException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }
}