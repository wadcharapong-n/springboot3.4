package com.north.springboot3.controller;

import com.north.springboot3.model.UserProfile;
import com.north.springboot3.repo.UserProfileRepository;
import com.north.springboot3.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {
    
    @Mock
    private UserProfileRepository userProfileRepository;

    private UserProfileService userProfileService;
    private UserProfileController userProfileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfileService = new UserProfileService(userProfileRepository);
        userProfileController = new UserProfileController(userProfileService);
    }

    @Test
    void getUserProfile() {
        UserProfile expectedProfile = new UserProfile();
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(expectedProfile));

        ResponseEntity<UserProfile> response = userProfileController.getUserProfile(1L);

        assertEquals(expectedProfile, response.getBody());
        verify(userProfileRepository).findById(1L);
    }

    @Test
    void createUserProfile() {
        UserProfile profileToCreate = new UserProfile();
        UserProfile createdProfile = new UserProfile();
        when(userProfileRepository.save(profileToCreate)).thenReturn(createdProfile);

        ResponseEntity<UserProfile> response = userProfileController.createUserProfile(profileToCreate);

        assertEquals(createdProfile, response.getBody());
        verify(userProfileRepository).save(profileToCreate);
    }

    @Test
    void updateUserProfile() {
        // Prepare test data
        Long profileId = 1L;
        UserProfile profileToUpdate = new UserProfile();
        profileToUpdate.setUsername("testuser");
        profileToUpdate.setEmail("test@email.com");
        profileToUpdate.setFullName("Test User");

        UserProfile existingProfile = new UserProfile();
        existingProfile.setId(profileId);

        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setId(profileId);
        updatedProfile.setUsername(profileToUpdate.getUsername());
        updatedProfile.setEmail(profileToUpdate.getEmail());
        updatedProfile.setFullName(profileToUpdate.getFullName());

        // Mock repository behavior
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedProfile);

        // Execute test
        ResponseEntity<UserProfile> response = userProfileController.updateUserProfile(profileId, profileToUpdate);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProfile, response.getBody());
        verify(userProfileRepository).findById(profileId);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void deleteUserProfile() {
        UserProfile existingProfile = new UserProfile();
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(existingProfile));

        ResponseEntity<Void> response = userProfileController.deleteUserProfile(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProfileRepository).findById(1L);
        verify(userProfileRepository).deleteById(1L);
    }
    @Test
    void getAllUserProfiles() {
        List<UserProfile> expectedProfiles = Arrays.asList(new UserProfile(), new UserProfile());
        when(userProfileRepository.findAll()).thenReturn(expectedProfiles);

        ResponseEntity<List<UserProfile>> response = userProfileController.getAllUserProfiles();

        assertEquals(expectedProfiles, response.getBody());
        verify(userProfileRepository).findAll();
    }

    @Test
    void updateUserProfile_NotFound() {
        Long profileId = 1L;
        UserProfile profileToUpdate = new UserProfile();
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class, () ->
                userProfileController.updateUserProfile(profileId, profileToUpdate));

        verify(userProfileRepository).findById(profileId);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void deleteUserProfile_NotFound() {
        Long profileId = 1L;
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class, () ->
                userProfileController.deleteUserProfile(profileId));

        verify(userProfileRepository).findById(profileId);
        verify(userProfileRepository, never()).deleteById(any());
    }
}