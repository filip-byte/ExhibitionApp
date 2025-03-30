package com.exhibitionapp.controller;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.dto.GalleryDTO;
import com.exhibitionapp.repository.GalleryArtworkRepository;
import com.exhibitionapp.repository.GalleryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtworkControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private GalleryArtworkRepository galleryArtworkRepository;

    @Autowired
    private RestTemplate externalRestTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(externalRestTemplate);
        galleryRepository.deleteAll();
        galleryArtworkRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mockServer.reset();
    }

    @Test
    void getArtworks_artic_success() {
        // Mock Artic API
        mockServer.expect(requestTo("http://localhost:8089/artworks/search?q=dogs&query%5Bterm%5D%5Bis_public_domain%5D=true&page=1&limit=5&fields=id,title,image_id"))
                .andRespond(withSuccess("{\"data\": [{\"id\": 123, \"title\": \"Test Artwork\", \"image_id\": \"abc123\"}]}", MediaType.APPLICATION_JSON));

        ResponseEntity<ArtworkDTO[]> response = restTemplate.getForEntity(
                "/api/artworks?source=Artic&q=dogs", ArtworkDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals("Test Artwork", response.getBody()[0].getTitle());
    }

    @Test
    void getArtworks_met_success() {
        // Mock MET API
        mockServer.expect(requestTo("http://localhost:8089/search?q=dog"))
                .andRespond(withSuccess("{\"total\": 1, \"objectIDs\": [456]}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://localhost:8089/objects/456"))
                .andRespond(withSuccess("{\"objectID\": 456, \"title\": \"MET Test\", \"primaryImageSmall\": \"http://met.jpg\"}", MediaType.APPLICATION_JSON));

        ResponseEntity<ArtworkDTO[]> response = restTemplate.getForEntity(
                "/api/artworks?source=MET&q=dog", ArtworkDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals("MET Test", response.getBody()[0].getTitle());
    }

    @Test
    void createGallery_success() {
        ResponseEntity<GalleryDTO> response = restTemplate.postForEntity(
                "/api/artworks/galleries?name=MyGallery&description=Art", null, GalleryDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("MyGallery", response.getBody().getName());
    }

    @Test
    void getGalleryArtworks_success() {
        GalleryDTO gallery = restTemplate.postForEntity(
                "/api/artworks/galleries?name=MyGallery", null, GalleryDTO.class).getBody();
        restTemplate.postForEntity(
                "/api/artworks/galleries/" + gallery.getId() + "/artworks?imageUrl=http://test.jpg", null, Void.class);

        ResponseEntity<ArtworkDTO[]> response = restTemplate.getForEntity(
                "/api/artworks/galleries/" + gallery.getId(), ArtworkDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
        assertEquals("http://test.jpg", response.getBody()[0].getImageUrl());
    }

    @Test
    void getAllGalleries_success() {
        restTemplate.postForEntity(
                "/api/artworks/galleries?name=Gallery1&description=First", null, GalleryDTO.class);
        restTemplate.postForEntity(
                "/api/artworks/galleries?name=Gallery2&description=Second", null, GalleryDTO.class);

        ResponseEntity<GalleryDTO[]> response = restTemplate.getForEntity(
                "/api/artworks/galleries", GalleryDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
        assertEquals("Gallery1", response.getBody()[0].getName());
        assertEquals("Gallery2", response.getBody()[1].getName());
    }
}