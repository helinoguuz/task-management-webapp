package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private JavaMailSender mailSender;

    // Email -> Token eşlemesi (geçici, RAM'de)
    private final Map<String, String> resetTokens = new HashMap<>();

    public void sendPasswordResetEmail(User user) {
        String token = UUID.randomUUID().toString();
        resetTokens.put(user.getEmail(), token);

        String resetLink = "http://localhost:8080/reset-password?email=" + user.getEmail() + "&token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("TaskTrack Password Reset Request");
        message.setText("Hi " + user.getName() + ",\n\n"
                + "To reset your password, please click the link below:\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "TaskTrack Team");

        mailSender.send(message);
    }

    public boolean verifyResetToken(String email, String token) {
        return resetTokens.containsKey(email) && resetTokens.get(email).equals(token);
    }

    public void clearResetToken(String email) {
        resetTokens.remove(email);
    }
}
