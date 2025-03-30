package com.exhibitionapp.service.external;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.external.MetSearchResponse;
import com.exhibitionapp.model.dto.external.MetObjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetArtworkProvider implements ArtworkProvider {

    private static final Logger logger = LoggerFactory.getLogger(MetArtworkProvider.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MetArtworkProvider(RestTemplate restTemplate, @Value("${met.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<ArtworkDTO> fetchArtworks(int page, int limit, String query) throws ArtworkProviderException {
        try {
            String searchUrl = String.format("%s/search?q=%s", baseUrl, query);
            logger.debug("Fetching MET search results: {}", searchUrl);
            MetSearchResponse searchResponse = restTemplate.getForObject(searchUrl, MetSearchResponse.class);
            if (searchResponse == null || searchResponse.getObjectIDs() == null) {
                throw new ArtworkProviderException("Received null response from MET search API");
            }

            int start = (page - 1) * limit;
            int end = Math.min(start + limit, searchResponse.getObjectIDs().size());
            if (start >= searchResponse.getObjectIDs().size()) {
                return List.of();
            }
            List<Integer> pageIds = searchResponse.getObjectIDs().subList(start, end);

            return pageIds.stream()
                    .map(id -> {
                        String objectUrl = String.format("%s/objects/%d", baseUrl, id);
                        logger.debug("Fetching MET object: {}", objectUrl);
                        MetObjectResponse response = restTemplate.getForObject(objectUrl, MetObjectResponse.class);
                        if (response == null) {
                            logger.warn("Null response for MET object ID: {}", id);
                            return null;
                        }
                        return new ArtworkDTO(response.getObjectID(), response.getTitle(), response.getPrimaryImageSmall());
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch from MET API: {}", e.getMessage(), e);
            throw new ArtworkProviderException("Error contacting MET API: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "MET";
    }
}