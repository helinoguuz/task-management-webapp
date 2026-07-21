package com.example.demo.controller;

import com.example.demo.model.Priority;
import com.example.demo.model.Status;
import com.example.demo.model.UserStory;
import com.example.demo.service.UserStoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserStoryRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "testuser", roles = {"USER"})
public class UserStoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserStoryService userStoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserStory story;

    @BeforeEach
    void setUp() {
        story = UserStory.builder()
                .id(1L)
                .title("Login Feature")
                .description("User can log in")
                .status(Status.OPEN)
                .priority(Priority.MEDIUM)
                .deadline(new Date())
                .build();
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(userStoryService.getAllStories()).thenReturn(List.of(story));

        mockMvc.perform(get("/api/userstories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Login Feature"));
    }

    @Test
    void getById_existing_shouldReturnStory() throws Exception {
        when(userStoryService.getStoryById(1L)).thenReturn(Optional.of(story));

        mockMvc.perform(get("/api/userstories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("User can log in"));
    }

    @Test
    void getById_notFound_shouldReturn404() throws Exception {
        when(userStoryService.getStoryById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/userstories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201() throws Exception {
        when(userStoryService.saveStory(any(UserStory.class))).thenReturn(story);

        mockMvc.perform(post("/api/userstories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(story)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Login Feature"));
    }

    @Test
    void update_existing_shouldReturnUpdated() throws Exception {
        when(userStoryService.getStoryById(1L)).thenReturn(Optional.of(story));
        when(userStoryService.saveStory(any(UserStory.class))).thenReturn(story);

        mockMvc.perform(put("/api/userstories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(story)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void update_notFound_shouldReturn404() throws Exception {
        when(userStoryService.getStoryById(100L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/userstories/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(story)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existing_shouldReturn204() throws Exception {
        when(userStoryService.getStoryById(1L)).thenReturn(Optional.of(story));

        mockMvc.perform(delete("/api/userstories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_shouldReturn404() throws Exception {
        when(userStoryService.getStoryById(404L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/userstories/404"))
                .andExpect(status().isNotFound());
    }
}
