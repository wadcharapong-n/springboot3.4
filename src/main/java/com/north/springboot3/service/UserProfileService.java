package com.north.springboot3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.north.springboot3.model.UserProfile;
import com.north.springboot3.repo.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfile getUserProfile(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found with id: " + id));
    }

    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public UserProfile updateUserProfile(Long id, UserProfile userProfile) {
        if (!userProfileRepository.existsById(id)) {
            throw new EntityNotFoundException("User profile not found with id: " + id);
        }
        userProfile.setId(id);
        return userProfileRepository.save(userProfile);
    }

    public void deleteUserProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new EntityNotFoundException("User profile not found with id: " + id);
        }
        userProfileRepository.deleteById(id);
    }

    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

}
