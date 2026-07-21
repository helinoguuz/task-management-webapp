package com.example.demo.service;

import com.example.demo.dao.IEmailVerification;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailVerificationService implements IEmailVerification {

    private final Map<String, String> verificationTokens = new HashMap<>();

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        verificationTokens.put(user.getEmail(), token);

        String verificationLink = "http://localhost:8080/verify?email=" + user.getEmail() + "&token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to TaskTrack - Registration Successful");
        message.setText("Hello " + user.getName() + ",\n\n"
                + "Your account has been successfully created.\n"
                + "You can optionally verify your email using the link below:\n"
                + verificationLink + "\n\n"
                + "Best regards,\n"
                + "The TaskTrack Team");

        mailSender.send(message);
        System.out.println("Verification email sent to: " + user.getEmail() + " | Token: " + token);
    }

    @Override
    public boolean verifyEmail(String email, String token) {
        return verificationTokens.containsKey(email) && verificationTokens.get(email).equals(token);
    }
}

