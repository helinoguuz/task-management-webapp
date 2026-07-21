package com.example.demo.service;

import com.example.demo.dao.IPasswordHasher;
import com.example.demo.model.UserRole;
import com.example.demo.repository.DatabaseManager;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final DatabaseManager databaseManager;
    private final EmailVerificationService emailVerificationService;
    private final IPasswordHasher passwordHasher;

    public AccountService(DatabaseManager databaseManager, EmailVerificationService emailVerificationService, IPasswordHasher passwordHasher) {
        this.databaseManager = databaseManager;
        this.passwordHasher = passwordHasher;
        this.emailVerificationService = emailVerificationService;
    }

    public boolean emailExists(String email) {
        return databaseManager.checkEmailExists(email.trim());
    }

    public boolean login(String email, String password) {
        email = email.trim();

        System.out.println(" Attempting login with email: " + email);
        User user = databaseManager.getUserByEmail(email);

        if (user == null) {
            System.out.println(" No user found with email: " + email);
            return false;
        }

        System.out.println(" Stored hashed password: " + user.getPassword());
        boolean result = passwordHasher.verifyPassword(password, user.getPassword());
        System.out.println(" Password match: " + result);
        return result;
    }

    public List<User> loadUsers() {
        return databaseManager.getAllUsers();
    }

    public void saveUsers(List<User> users) {
        users.forEach(databaseManager::storeUser);
    }

    public List<User> findUsersByName(String name) {
        return databaseManager.findByName(name);
    }

    public User findUserByEmail(String email) {
        return databaseManager.getUserByEmail(email.trim());
    }

    public boolean registerUser(String name, String email, String password) {
        email = email.trim();

        if (databaseManager.checkEmailExists(email)) {
            return false;
        }

        String hashedPassword = passwordHasher.hashPassword(password);
        System.out.println(" Registering user with hashed password: " + hashedPassword);

        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(hashedPassword)
                .role(UserRole.NORMAL_USER)
                .build();

        boolean success = databaseManager.storeUser(newUser);

        if (success){
            emailVerificationService.sendVerificationEmail(newUser);
        }
        return success;
    }
    public User getUserByEmail(String email) {
        return databaseManager.getUserByEmail(email.trim());
    }

    public boolean updateProfile(String currentEmail, String newName, String newEmail) {
        User user = databaseManager.getUserByEmail(currentEmail.trim());
        if (user == null) return false;

        user.setName(newName);
        user.setEmail(newEmail.trim());

        return databaseManager.updateUser(user);
    }

    public List<User> getAllUsers() {

        return databaseManager.getAllUsers();
    }

    public void updateUserRole(String email, UserRole newRole) {
        User user = databaseManager.getUserByEmail(email.trim());
        if (user != null) {
            user.setRole(newRole);
            databaseManager.updateUser(user);
        }
    }
    public void saveUser(User user) {
        databaseManager.updateUser(user); // varsa, yoksa zaten save yapıyor
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);
        if (user == null) return false;

        if (passwordHasher.verifyPassword(oldPassword, user.getPassword())) {
            user.setPassword(passwordHasher.hashPassword(newPassword));
            return databaseManager.updateUser(user);
        }
        return false;
    }
    public void updatePassword(String email, String newPassword) {
        User user = databaseManager.getUserByEmail(email);
        if (user != null) {
            String hashed = passwordHasher.hashPassword(newPassword);
            user.setPassword(hashed);
            databaseManager.storeUser(user);
        }
    }
    public User getUserById(Long id) {
        return databaseManager.getUserById(id);
    }


}

