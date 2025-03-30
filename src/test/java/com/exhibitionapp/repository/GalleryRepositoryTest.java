package com.exhibitionapp.repository;

import com.exhibitionapp.model.entity.Gallery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GalleryRepositoryTest {

    @Autowired
    private GalleryRepository galleryRepository;

    @Test
    void saveAndFindByName_success() {
        Gallery gallery = new Gallery();
        gallery.setName("MyGallery");
        gallery.setDescription("Art");
        galleryRepository.save(gallery);

        Optional<Gallery> result = galleryRepository.findByName("MyGallery");
        assertTrue(result.isPresent());
        assertEquals("MyGallery", result.get().getName());
    }

    @Test
    void findByName_notFound_returnsEmpty() {
        Optional<Gallery> result = galleryRepository.findByName("NonExistent");
        assertFalse(result.isPresent());
    }
}