package com.exhibitionapp.service.external;

import com.exhibitionapp.model.dto.ArtworkDTO;
import java.util.List;

public interface ArtworkProvider {
    List<ArtworkDTO> fetchArtworks(int page, int limit, String query) throws ArtworkProviderException;
    String getProviderName();
}