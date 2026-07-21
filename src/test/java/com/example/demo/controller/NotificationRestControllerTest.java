package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "testuser", roles = {"USER"})

public class NotificationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationRepository notificationRepository;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleNotification = Notification.builder()
                .id(1L)
                .recipientEmail("user@example.com")
                .message("You have a task")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    @Test
    void getNotifications_shouldReturnNotificationList() throws Exception {
        when(notificationRepository.findByRecipientEmailOrderByCreatedAtDesc("user@example.com"))
                .thenReturn(List.of(sampleNotification));

        mockMvc.perform(get("/api/notifications")
                        .param("email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("You have a task"));
    }

    @Test
    void getUnreadNotifications_shouldReturnOnlyUnread() throws Exception {
        when(notificationRepository.findByRecipientEmailAndIsReadFalse("user@example.com"))
                .thenReturn(List.of(sampleNotification));

        mockMvc.perform(get("/api/notifications/unread")
                        .param("email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    void markAsRead_existingUnread_shouldSucceed() throws Exception {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(sampleNotification));

        mockMvc.perform(post("/api/notifications/1/markAsRead"))
                .andExpect(status().isOk())
                .andExpect(content().string("Marked as read"));

        assert sampleNotification.isRead(); // çünkü controller içinde setRead(true) var
    }

    @Test
    void markAsRead_alreadyRead_shouldReturnBadRequest() throws Exception {
        sampleNotification.setRead(true);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(sampleNotification));

        mockMvc.perform(post("/api/notifications/1/markAsRead"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Notification already marked as read."));
    }

    @Test
    void markAsRead_notFound_shouldReturn404() throws Exception {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/notifications/999/markAsRead"))
                .andExpect(status().isNotFound());
    }
}
