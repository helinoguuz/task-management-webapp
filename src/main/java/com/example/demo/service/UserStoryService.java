package com.example.demo.service;

import com.example.demo.model.UserStory;
import com.example.demo.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;

    public List<UserStory> getAllStories() {
        return userStoryRepository.findAll();
    }

    public Optional<UserStory> getStoryById(Long id) {
        return userStoryRepository.findById(id);
    }

    public UserStory saveStory(UserStory story) {
        return userStoryRepository.save(story);
    }

    public void deleteStory(Long id) {
        userStoryRepository.deleteById(id);
    }
}
