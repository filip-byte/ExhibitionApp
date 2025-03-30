package com.exhibitionapp.service.impl;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.entity.Artwork;
import com.exhibitionapp.repository.ArtworkRepository;
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

    @Autowired
    public ArtworkServiceImpl(List<ArtworkProvider> providers, ArtworkRepository artworkRepository) {
        this.providers = providers;
        this.artworkRepository = artworkRepository;
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
}