package com.example.demo.service;

import com.example.demo.dao.IPasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHasher implements IPasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hashPassword(String password) {
        String hashed = encoder.encode(password);
        System.out.println("🔐 Hashing password: " + password); // print statements zum Debuggen
        System.out.println("➡️  Hashed: " + hashed); // print statements zum Debuggen
        return hashed;
    }

    @Override
    public boolean verifyPassword(String password, String storedHash) {
        boolean result = encoder.matches(password, storedHash);
        System.out.println("🔐 Verifying password: " + password); // print statements zum Debuggen
        System.out.println("🔒 Stored hash: " + storedHash); // print statements zum Debuggen
        System.out.println("✅ Password match: " + result); // print statements zum Debuggen
        return result;
    }
}
