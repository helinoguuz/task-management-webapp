package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationRepository notificationRepository;

    public NotificationRestController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam String email) {
        List<Notification> notifications = notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam String email) {
        List<Notification> notifications = notificationRepository.findByRecipientEmailAndIsReadFalse(email);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/markAsRead")
    @Transactional
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Notification notification = optional.get();
        if (notification.isRead()) {
            return ResponseEntity.badRequest().body("Notification already marked as read.");
        }

        notification.setRead(true);
        return ResponseEntity.ok("Marked as read");
    }
}
