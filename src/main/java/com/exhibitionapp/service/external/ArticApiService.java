package com.exhibitionapp.service.external;

import com.exhibitionapp.model.dto.external.ArticApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ArticApiService {

    private static final Logger logger = LoggerFactory.getLogger(ArticApiService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ArticApiService(RestTemplate restTemplate, @Value("${artic.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ArticApiResponse getArtworksFromArtic(int page, int limit, String query) {
        String url = String.format(
                "%s/artworks/search?q=%s&query[term][is_public_domain]=true&page=%d&limit=%d&fields=id,title,image_id",
                baseUrl, query, page, limit
        );

        try {
            logger.debug("Fetching artworks from Artic API: {}", url);
            ArticApiResponse response = restTemplate.getForObject(url, ArticApiResponse.class);
            if (response == null) {
                throw new ArticApiException("Received null response from Artic API", HttpStatus.SERVICE_UNAVAILABLE);
            }
            return response;
        } catch (RestClientException e) {
            logger.error("Failed to fetch artworks from Artic API: {}", e.getMessage(), e);
            throw new ArticApiException("Error contacting Artic API: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}