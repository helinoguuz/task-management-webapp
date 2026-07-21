package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.Priority;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskQueryService {

    private final TaskRepository taskRepository;

    public TaskQueryService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByProject(String projectName) {

        throw new UnsupportedOperationException("Use findByProjectId(Long id) instead.");
    }

    public List<Task> getTasksByStatus(String status) {
        TaskStatus statusEnum = TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByStatus(statusEnum);
    }

    public List<Task> getTasksByPriority(String priority) {
        Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
        return taskRepository.findByPriority(priorityEnum);
    }
}
