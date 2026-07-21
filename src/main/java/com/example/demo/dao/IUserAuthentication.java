package com.example.demo.dao;

public interface IUserAuthentication {
    boolean login(String email, String rawPassword);
    void logout();
}