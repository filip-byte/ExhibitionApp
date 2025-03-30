package com.exhibitionapp.service.impl;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.GalleryDTO;
import com.exhibitionapp.model.entity.Gallery;
import com.exhibitionapp.model.entity.GalleryArtwork;
import com.exhibitionapp.repository.GalleryArtworkRepository;
import com.exhibitionapp.repository.GalleryRepository;
import com.exhibitionapp.service.ArtworkService;
import com.exhibitionapp.service.external.ArticArtworkProvider;
import com.exhibitionapp.service.external.MetArtworkProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtworkServiceImpl implements ArtworkService {

    private final ArticArtworkProvider articProvider;
    private final MetArtworkProvider metProvider;
    private final GalleryRepository galleryRepository;
    private final GalleryArtworkRepository galleryArtworkRepository;

    public ArtworkServiceImpl(ArticArtworkProvider articProvider, MetArtworkProvider metProvider,
                              GalleryRepository galleryRepository, GalleryArtworkRepository galleryArtworkRepository) {
        this.articProvider = articProvider;
        this.metProvider = metProvider;
        this.galleryRepository = galleryRepository;
        this.galleryArtworkRepository = galleryArtworkRepository;
    }

    @Override
    public List<ArtworkDTO> getArtworks(String source, String query, int page, int limit) {
        switch (source.toLowerCase()) {
            case "artic":
                return articProvider.fetchArtworks(page, limit, query);
            case "met":
                return metProvider.fetchArtworks(page, limit, query);
            default:
                throw new IllegalArgumentException("Invalid source: " + source);
        }
    }

    @Override
    public Gallery createGallery(String name, String description) {
        if (galleryRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("Gallery with name " + name + " already exists");
        }
        Gallery gallery = new Gallery();
        gallery.setName(name);
        gallery.setDescription(description);
        return galleryRepository.save(gallery);
    }

    @Override
    public void addArtworkToGallery(Long galleryId, String imageUrl) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + galleryId));
        if (galleryArtworkRepository.existsByGalleryIdAndImageUrl(galleryId, imageUrl)) {
            return; // Already exists, no-op
        }
        GalleryArtwork artwork = new GalleryArtwork();
        artwork.setGallery(gallery);
        artwork.setImageUrl(imageUrl);
        galleryArtworkRepository.save(artwork);
    }

    @Override
    public List<ArtworkDTO> getGalleryArtworks(Long galleryId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + galleryId));
        return galleryArtworkRepository.findByGallery(gallery)
                .stream()
                .map(ga -> new ArtworkDTO(ga.getId().intValue(), null, ga.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GalleryDTO> getAllGalleries() {
        return galleryRepository.findAll()
                .stream()
                .map(g -> new GalleryDTO(g.getId(), g.getName(), g.getDescription()))
                .collect(Collectors.toList());
    }
}