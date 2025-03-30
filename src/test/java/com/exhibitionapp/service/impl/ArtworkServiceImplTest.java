package com.exhibitionapp.service.impl;

import com.exhibitionapp.model.dto.GalleryDTO;
import com.exhibitionapp.model.entity.Gallery;
import com.exhibitionapp.repository.GalleryArtworkRepository;
import com.exhibitionapp.repository.GalleryRepository;
import com.exhibitionapp.service.external.ArticArtworkProvider;
import com.exhibitionapp.service.external.MetArtworkProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    void createGallery_success() {
        when(galleryRepository.findByName("MyGallery")).thenReturn(Optional.empty());
        Gallery savedGallery = new Gallery();
        savedGallery.setId(1L);
        savedGallery.setName("MyGallery");
        when(galleryRepository.save(any(Gallery.class))).thenReturn(savedGallery);

        GalleryDTO result = artworkService.createGallery("MyGallery", "Art");
        assertEquals("MyGallery", result.getName());
    }

    @Test
    void addArtworkToGallery_galleryNotFound_throwsException() {
        when(galleryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> artworkService.addArtworkToGallery(1L, "http://test.jpg"));
    }
}