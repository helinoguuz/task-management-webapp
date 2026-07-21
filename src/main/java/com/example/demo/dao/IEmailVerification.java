package com.example.demo.dao;

import com.example.demo.model.User;

public interface IEmailVerification {
    void sendVerificationEmail(User user);
    boolean verifyEmail(String email, String token);
}
