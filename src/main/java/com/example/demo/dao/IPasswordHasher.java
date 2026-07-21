package com.example.demo.dao;

public interface IPasswordHasher {
    String hashPassword(String password);
    boolean verifyPassword(String password, String storedHash);
}
