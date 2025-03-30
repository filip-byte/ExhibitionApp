package com.exhibitionapp.service.external;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.external.ArticApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticArtworkProvider implements ArtworkProvider {

    private static final Logger logger = LoggerFactory.getLogger(ArticArtworkProvider.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ArticArtworkProvider(RestTemplate restTemplate,
                                @Value("${artic.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<ArtworkDTO> fetchArtworks(int page, int limit, String query) throws ArtworkProviderException {
        String url = String.format(
                "%s/artworks/search?q=%s&query[term][is_public_domain]=true&page=%d&limit=%d&fields=id,title,image_id",
                baseUrl, query, page, limit
        );
        try {
            logger.debug("Fetching artworks from Artic API: {}", url);
            ArticApiResponse response = restTemplate.getForObject(url, ArticApiResponse.class);
            if (response == null || response.getData() == null) {
                throw new ArtworkProviderException("Received null response from Artic API");
            }
            return response.getData().stream()
                    .map(articArtwork -> {
                        String imageUrl = String.format(
                                "https://www.artic.edu/iiif/2/%s/full/843,/0/default.jpg",
                                articArtwork.getImage_id()
                        );
                        return new ArtworkDTO(articArtwork.getId(), articArtwork.getTitle(), imageUrl);
                    })
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            logger.error("Failed to fetch from Artic API: {}", e.getMessage(), e);
            throw new ArtworkProviderException("Error contacting Artic API: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "Artic";
    }
}