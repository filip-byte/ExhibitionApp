package com.exhibitionapp.service.external;

import com.exhibitionapp.model.dto.ArtworkDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class MetArtworkProviderTest {

    private MockRestServiceServer mockServer;
    private MetArtworkProvider provider;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        provider = new MetArtworkProvider(restTemplate, "http://localhost");
    }

    @Test
    void fetchArtworks_success() {
        mockServer.expect(requestTo("http://localhost/search?q=dog"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"total\": 1, \"objectIDs\": [123]}", MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("http://localhost/objects/123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"objectID\": 123, \"title\": \"Test MET Artwork\", \"primaryImageSmall\": \"http://test.jpg\"}", MediaType.APPLICATION_JSON));

        List<ArtworkDTO> artworks = provider.fetchArtworks(1, 6, "dog");
        assertEquals(1, artworks.size());
        assertEquals(123, artworks.get(0).getId());
        assertEquals("Test MET Artwork", artworks.get(0).getTitle());
        assertEquals("http://test.jpg", artworks.get(0).getImageUrl());
    }

    @Test
    void fetchArtworks_emptyPage_returnsEmptyList() {
        mockServer.expect(requestTo("http://localhost/search?q=dog"))
                .andRespond(withSuccess("{\"total\": 1, \"objectIDs\": [123]}", MediaType.APPLICATION_JSON));

        List<ArtworkDTO> artworks = provider.fetchArtworks(2, 6, "dog"); // Page 2, limit 6, only 1 result
        assertTrue(artworks.isEmpty());
    }
}