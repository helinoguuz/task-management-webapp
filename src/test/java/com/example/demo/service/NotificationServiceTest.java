package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        notificationService = new NotificationService(notificationRepository);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testSend_shouldSaveNotificationToRepository() {
        notificationService.send("user@example.com", "Welcome!");

        verify(notificationRepository, times(1)).save(argThat(n ->
                n.getRecipientEmail().equals("user@example.com") &&
                        n.getMessage().equals("Welcome!") &&
                        !n.isRead() &&
                        n.getCreatedAt() != null
        ));
    }

    @Test
    void testSendTaskCreationNotification_shouldPrintToConsole() {
        notificationService.sendTaskCreationNotification(101L);
        assertTrue(outContent.toString().contains("📬 Task created: ID = 101"));
    }

    @Test
    void testSendTaskUpdateNotification_shouldPrintToConsole() {
        notificationService.sendTaskUpdateNotification(200L, "COMPLETED");
        assertTrue(outContent.toString().contains("🔄 Task updated: ID = 200, new status = COMPLETED"));
    }

    @Test
    void testSendTaskAssignmentNotification_shouldPrintToConsole() {
        notificationService.sendTaskAssignmentNotification(300L, 42L);
        assertTrue(outContent.toString().contains("👤 Task ID 300 assigned to user ID 42"));
    }

    @Test
    void testSendTaskCompletionNotification_shouldPrintToConsole() {
        notificationService.sendTaskCompletionNotification(400L);
        assertTrue(outContent.toString().contains("✅ Task ID 400 marked as completed"));
    }

    @Test
    void testSendTaskDeletionNotification_shouldPrintToConsole() {
        notificationService.sendTaskDeletionNotification(500L);
        assertTrue(outContent.toString().contains("🗑️ Task ID 500 was deleted"));
    }

    @Test
    void testSend_withNullInputs_shouldStillSave() {
        notificationService.send(null, null);
        verify(notificationRepository).save(argThat(n ->
                n.getRecipientEmail() == null &&
                        n.getMessage() == null
        ));
    }
}
