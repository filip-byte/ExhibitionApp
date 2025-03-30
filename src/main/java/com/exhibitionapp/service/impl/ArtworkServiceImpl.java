package com.exhibitionapp.service.impl;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.external.ArticApiResponse;
import com.exhibitionapp.model.entity.Artwork;
import com.exhibitionapp.repository.ArtworkRepository;
import com.exhibitionapp.service.ArtworkService;
import com.exhibitionapp.service.external.ArticApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ArtworkService, responsible for fetching and transforming artwork data.
 */
@Service
public class ArtworkServiceImpl implements ArtworkService {

    private final ArticApiService articApiService;
    private final ArtworkRepository artworkRepository;

    @Autowired
    public ArtworkServiceImpl(ArticApiService articApiService, ArtworkRepository artworkRepository) {
        this.articApiService = articApiService;
        this.artworkRepository = artworkRepository;
    }

    @Override
    public List<ArtworkDTO> getArtworks(int page, int limit, String query) {
        // Fetch data from the Artic API
        ArticApiResponse response = articApiService.getArtworksFromArtic(page, limit, query);

        // Transform ArticArtwork objects to ArtworkDTOs
        List<ArtworkDTO> artworkDTOs = response.getData().stream()
                .map(articArtwork -> {
                    ArtworkDTO dto = new ArtworkDTO();
                    dto.setId(articArtwork.getId());
                    dto.setTitle(articArtwork.getTitle());
                    dto.setImageUrl(buildImageUrl(articArtwork.getImage_id(), response.getConfig().getIiif_url()));
                    return dto;
                })
                .collect(Collectors.toList());

        // Optional: Save to database for caching (uncomment to enable)
        // saveToDatabase(response.getData());

        return artworkDTOs;
    }

    /**
     * Constructs the full image URL using the image ID and IIIF base URL.
     *
     * @param imageId the image ID from the Artic API
     * @param iiifUrl the IIIF base URL from the API response
     * @return the complete image URL
     */
    private String buildImageUrl(String imageId, String iiifUrl) {
        return String.format("%s/%s/full/843,/0/default.jpg", iiifUrl, imageId);
    }

    /**
     * Saves Artic API artworks to the PostgreSQL database (optional caching).
     *
     * @param articArtworks list of artworks from the API
     */
    private void saveToDatabase(List<com.exhibitionapp.model.dto.external.ArticArtwork> articArtworks) {
        List<Artwork> entities = articArtworks.stream()
                .map(articArtwork -> Artwork.builder()
                        .id(articArtwork.getId())
                        .title(articArtwork.getTitle())
                        .imageId(articArtwork.getImage_id())
                        .build())
                .collect(Collectors.toList());
        artworkRepository.saveAll(entities);
    }
}