package com.exhibitionapp.service.impl;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.GalleryDTO;
import com.exhibitionapp.model.entity.Artwork;
import com.exhibitionapp.model.entity.Gallery;
import com.exhibitionapp.model.entity.GalleryArtwork;
import com.exhibitionapp.repository.ArtworkRepository;
import com.exhibitionapp.repository.GalleryArtworkRepository;
import com.exhibitionapp.repository.GalleryRepository;
import com.exhibitionapp.service.ArtworkService;
import com.exhibitionapp.service.external.ArtworkProvider;
import com.exhibitionapp.service.external.ArtworkProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtworkServiceImpl implements ArtworkService {

    private final List<ArtworkProvider> providers;
    private final ArtworkRepository artworkRepository;
    private final GalleryRepository galleryRepository;
    private final GalleryArtworkRepository galleryArtworkRepository;

    @Autowired
    public ArtworkServiceImpl(List<ArtworkProvider> providers,
                              ArtworkRepository artworkRepository,
                              GalleryRepository galleryRepository,
                              GalleryArtworkRepository galleryArtworkRepository) {
        this.providers = providers;
        this.artworkRepository = artworkRepository;
        this.galleryRepository = galleryRepository;
        this.galleryArtworkRepository = galleryArtworkRepository;
    }

    @Override
    public List<ArtworkDTO> getArtworks(int page, int limit, String query, String source) {
        ArtworkProvider provider = providers.stream()
                .filter(p -> p.getProviderName().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown source: " + source));
        try {
            return provider.fetchArtworks(page, limit, query);
        } catch (ArtworkProviderException e) {
            throw new RuntimeException("Failed to fetch artworks from " + source + ": " + e.getMessage(), e);
        }
    }

    @Override
    public GalleryDTO createGallery(String name, String description) {
        Gallery gallery = Gallery.builder()
                .name(name)
                .description(description)
                .build();
        gallery = galleryRepository.save(gallery);
        return new GalleryDTO(gallery.getId(), gallery.getName(), gallery.getDescription());
    }

    @Override
    public void addArtworkToGallery(Long galleryId, String imageUrl) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + galleryId));
        GalleryArtwork galleryArtwork = GalleryArtwork.builder()
                .gallery(gallery)
                .imageUrl(imageUrl)
                .build();
        galleryArtworkRepository.save(galleryArtwork);
    }
}