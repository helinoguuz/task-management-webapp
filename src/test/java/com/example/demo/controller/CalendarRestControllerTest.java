package com.example.demo.controller;

import com.example.demo.model.CalendarEntry;
import com.example.demo.service.CalendarService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(CalendarRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "testuser", roles = {"USER"})
public class CalendarRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarService calendarService;

    @Autowired
    private ObjectMapper objectMapper;

    private CalendarEntry entry;

    @BeforeEach
    void setUp() {
        entry = new CalendarEntry();
        entry.setId(1L);
        entry.setTitle("Sprint Demo");
        entry.setStartTime(LocalDateTime.of(2025, 7, 20, 14, 0));
        entry.setEndTime(LocalDateTime.of(2025, 7, 20, 15, 0));
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(calendarService.getAllEntries()).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/calendar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sprint Demo"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        when(calendarService.saveEntry(any(CalendarEntry.class))).thenReturn(entry);

        mockMvc.perform(post("/api/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sprint Demo"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(calendarService).deleteEntry(1L);

        mockMvc.perform(delete("/api/calendar/1"))
                .andExpect(status().isNoContent());
    }
}
