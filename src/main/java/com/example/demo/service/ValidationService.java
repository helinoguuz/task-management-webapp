package com.example.demo.service;

import com.example.demo.model.Task;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public boolean isValid(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            return false;
        }

        if (task.getPriority() == null) {
            return false;
        }

        if (task.getStatus() == null) {
            return false;
        }

        return true;
    }
}
