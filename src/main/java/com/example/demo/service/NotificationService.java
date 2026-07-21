package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    public void send(String recipientEmail, String message) {
        logger.info("📢 New notification to {}: {}", recipientEmail, message);

        Notification notification = Notification.builder()
                .recipientEmail(recipientEmail)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }


    public void notifyTaskCreation(Long taskId, String taskTitle) {
        logger.info("Task created: [{}] {}", taskId, taskTitle);
    }

    public void sendTaskCreationNotification(Long taskId) {
        System.out.println("📬 Task created: ID = " + taskId);
    }

    public void sendTaskUpdateNotification(Long taskId, String newStatus) {
        System.out.println("🔄 Task updated: ID = " + taskId + ", new status = " + newStatus);
    }

    public void sendTaskAssignmentNotification(Long taskId, Long userId) {
        System.out.println("👤 Task ID " + taskId + " assigned to user ID " + userId);
    }

    public void sendTaskCompletionNotification(Long taskId) {
        System.out.println("✅ Task ID " + taskId + " marked as completed");
    }

    public void sendTaskDeletionNotification(Long taskId) {
        System.out.println("🗑️ Task ID " + taskId + " was deleted");
    }
}
