package com.example.demo.dao;

public interface IAccountManagement {
    boolean registerUser(String name, String email, String rawPassword);
    boolean userExists(String email);
}
