package com.example.demo.service;

import com.example.demo.model.Priority;
import com.example.demo.model.Status;
import com.example.demo.model.UserStory;
import com.example.demo.repository.UserStoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStoryServiceTest {

    private UserStoryRepository userStoryRepository;
    private UserStoryService userStoryService;

    private UserStory story;

    @BeforeEach
    void setUp() {
        userStoryRepository = mock(UserStoryRepository.class);
        userStoryService = new UserStoryService(userStoryRepository);

        story = UserStory.builder()
                .id(1L)
                .title("Add dark mode")
                .description("Users can switch to dark mode")
                .status(Status.OPEN)
                .priority(Priority.HIGH)
                .deadline(new Date())
                .build();
    }

    @Test
    void getAllStories_shouldReturnList() {
        when(userStoryRepository.findAll()).thenReturn(List.of(story));

        List<UserStory> result = userStoryService.getAllStories();

        assertEquals(1, result.size());
        assertEquals("Add dark mode", result.get(0).getTitle());
    }

    @Test
    void getStoryById_existing_shouldReturnStory() {
        when(userStoryRepository.findById(1L)).thenReturn(Optional.of(story));

        Optional<UserStory> result = userStoryService.getStoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Add dark mode", result.get().getTitle());
    }

    @Test
    void getStoryById_notFound_shouldReturnEmpty() {
        when(userStoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserStory> result = userStoryService.getStoryById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void saveStory_shouldReturnSavedStory() {
        when(userStoryRepository.save(any(UserStory.class))).thenReturn(story);

        UserStory saved = userStoryService.saveStory(story);

        assertNotNull(saved);
        assertEquals("Add dark mode", saved.getTitle());
    }

    @Test
    void deleteStory_shouldCallRepository() {
        doNothing().when(userStoryRepository).deleteById(1L);

        userStoryService.deleteStory(1L);

        verify(userStoryRepository).deleteById(1L);
    }
}
