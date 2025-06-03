package com.north.springboot3.service;

import com.north.springboot3.model.UserProfile;
import com.north.springboot3.repo.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
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
        return userProfileRepository.findById(id)
                .map(existingProfile -> {
                    userProfile.setId(id);
                    return userProfileRepository.save(userProfile);
                })
                .orElseThrow(() -> new EntityNotFoundException("User profile not found with id: " + id));
    }

    public void deleteUserProfile(Long id) {
        userProfileRepository.findById(id)
                .ifPresentOrElse(
                        profile -> userProfileRepository.deleteById(id),
                        () -> {
                            throw new EntityNotFoundException("User profile not found with id: " + id);
                        }
                );
    }

    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

}
