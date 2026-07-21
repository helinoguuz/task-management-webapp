package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public boolean isAuthorizedToCreateTask(User user) {
        return isAdminOrManager(user);
    }

    public boolean isAuthorizedToUpdateTask(User user) {
        return isAdminOrManager(user);
    }

    public boolean isAuthorizedToAssignTask(User user) {
        return isManager(user);
    }

    public boolean isAuthorizedToDeleteTask(User user) {
        return isAdmin(user);
    }

    public boolean isAuthorizedToViewTask(User user) {
        return user != null;
    }

    private boolean isAdminOrManager(User user) {
        return user != null && (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.MANAGER);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    private boolean isManager(User user) {
        return user != null && user.getRole() == UserRole.MANAGER;
    }
}
