package com.exhibitionapp.service.external;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.external.ArticApiResponse;
import com.exhibitionapp.model.dto.external.ArticArtwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ArticArtworkProviderTest {

    private MockRestServiceServer mockServer;
    private ArticArtworkProvider provider;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        provider = new ArticArtworkProvider(restTemplate, "http://localhost");
    }

    @Test
    void fetchArtworks_success() {
        ArticArtwork artwork = new ArticArtwork();
        artwork.setId(123);
        artwork.setTitle("Test Artwork");
        artwork.setImage_id("abc123");
        ArticApiResponse response = new ArticApiResponse();
        response.setData(Collections.singletonList(artwork));

        mockServer.expect(requestTo("http://localhost/artworks/search?q=dogs&query%5Bterm%5D%5Bis_public_domain%5D=true&page=1&limit=5&fields=id,title,image_id"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"data\": [{\"id\": 123, \"title\": \"Test Artwork\", \"image_id\": \"abc123\"}]}", MediaType.APPLICATION_JSON));

        List<ArtworkDTO> artworks = provider.fetchArtworks(1, 5, "dogs");
        assertEquals(1, artworks.size());
        assertEquals(123, artworks.get(0).getId());
        assertEquals("Test Artwork", artworks.get(0).getTitle());
        assertEquals("https://www.artic.edu/iiif/2/abc123/full/843,/0/default.jpg", artworks.get(0).getImageUrl());
    }

    @Test
    void fetchArtworks_failure_throwsException() {
        mockServer.expect(requestTo("http://localhost/artworks/search?q=dogs&query%5Bterm%5D%5Bis_public_domain%5D=true&page=1&limit=5&fields=id,title,image_id"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(ArtworkProviderException.class, () -> provider.fetchArtworks(1, 5, "dogs"));
    }
}