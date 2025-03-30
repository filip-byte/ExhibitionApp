package com.exhibitionapp.service.impl;

import com.exhibitionapp.model.dto.ArtworkDTO;
import com.exhibitionapp.model.entity.Gallery;
import com.exhibitionapp.model.entity.GalleryArtwork;
import com.exhibitionapp.repository.GalleryArtworkRepository;
import com.exhibitionapp.repository.GalleryRepository;
import com.exhibitionapp.service.external.ArticArtworkProvider;
import com.exhibitionapp.service.external.MetArtworkProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArtworkServiceImplTest {

    @Mock
    private ArticArtworkProvider articProvider;

    @Mock
    private MetArtworkProvider metProvider;

    @Mock
    private GalleryRepository galleryRepository;

    @Mock
    private GalleryArtworkRepository galleryArtworkRepository;

    @InjectMocks
    private ArtworkServiceImpl artworkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getArtworks_artic_success() {
        ArtworkDTO artwork = new ArtworkDTO(123, "Test Artwork", "http://test.jpg");
        when(articProvider.fetchArtworks(1, 5, "dogs")).thenReturn(Collections.singletonList(artwork));

        List<ArtworkDTO> result = artworkService.getArtworks("Artic", "dogs", 1, 5);
        assertEquals(1, result.size());
        assertEquals("Test Artwork", result.get(0).getTitle());
    }

    @Test
    void getArtworks_invalidSource_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> artworkService.getArtworks("Invalid", "dogs", 1, 5));
    }

    @Test
    void createGallery_success() {
        when(galleryRepository.findByName("MyGallery")).thenReturn(Optional.empty());
        Gallery savedGallery = new Gallery();
        savedGallery.setId(1L);
        savedGallery.setName("MyGallery");
        savedGallery.setDescription("Art");
        when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);

        Gallery result = artworkService.createGallery("MyGallery", "Art");
        assertEquals("MyGallery", result.getName());
    }

    @Test
    void createGallery_duplicateName_throwsException() {
        when(galleryRepository.findByName("MyGallery")).thenReturn(Optional.of(new Gallery()));
        assertThrows(IllegalStateException.class, () -> artworkService.createGallery("MyGallery", "Art"));
    }

    @Test
    void addArtworkToGallery_success() {
        Gallery gallery = new Gallery();
        gallery.setId(1L);
        when(galleryRepository.findById(1L)).thenReturn(Optional.of(gallery));
        when(galleryArtworkRepository.existsByGalleryIdAndImageUrl(1L, "http://test.jpg")).thenReturn(false);

        artworkService.addArtworkToGallery(1L, "http://test.jpg");
        verify(galleryArtworkRepository).save(any(GalleryArtwork.class));
    }

    @Test
    void addArtworkToGallery_galleryNotFound_throwsException() {
        when(galleryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> artworkService.addArtworkToGallery(1L, "http://test.jpg"));
    }

    @Test
    void getGalleryArtworks_success_autoincrement() {
        Gallery gallery = new Gallery();
        gallery.setId(1L);
        GalleryArtwork artwork = new GalleryArtwork();
        artwork.setId(1L); // Ensure ID is set
        artwork.setImageUrl("http://test.jpg");
        when(galleryRepository.findById(1L)).thenReturn(Optional.of(gallery));
        when(galleryArtworkRepository.findByGallery(gallery)).thenReturn(Collections.singletonList(artwork));

        List<ArtworkDTO> result = artworkService.getGalleryArtworks(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("http://test.jpg", result.get(0).getImageUrl());
    }
}