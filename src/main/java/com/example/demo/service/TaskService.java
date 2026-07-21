package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserStoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TaskService {


    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final TaskRepository taskRepository;

    private final UserStoryRepository userStoryRepository;

    public Task createTask(String title, String description, Priority priority, User assignedUser, Long userStoryId) {
        if (!userStoryRepository.existsById(userStoryId)) {
            throw new IllegalArgumentException("User Story existiert nicht.");
        }

        Task task = Task.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .status(TaskStatus.NEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .assignedUser(assignedUser)
                .userStoryId(userStoryId)
                .build();

        Task savedTask = taskRepository.save(task);
        notificationService.notifyTaskCreation(savedTask.getId(), savedTask.getTitle());
        return savedTask;
    }

    // ✅ Added this for the REST controller
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByPriority(Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setPriority(updatedTask.getPriority());
            task.setStatus(updatedTask.getStatus());
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        }).orElse(null);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task updateAssignedUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        User newUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        task.setAssignedUser(newUser);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task uploadFileToTask(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            String filePath = uploadDir + uniqueFilename;

            File dest = new File(filePath);
            file.transferTo(dest);

            TaskFile taskFile = TaskFile.builder()
                    .fileName(originalFilename)
                    .storedFileName(uniqueFilename)
                    .fileType(file.getContentType())
                    .uploadedAt(LocalDateTime.now())
                    .filePath(filePath)
                    .task(task)
                    .build();

            task.getFiles().add(taskFile);
            return taskRepository.save(task);
        } catch (IOException e) {
            throw new RuntimeException("Dateiupload fehlgeschlagen: " + e.getMessage());
        }
    }


    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Task getTask(String number) {
        try {
            Long id = Long.parseLong(number);
            return taskRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Task addFileToTask(Long taskId, String fileName, String fileType) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        TaskFile file = TaskFile.builder()
                .fileName(fileName)
                .fileType(fileType)
                .uploadedAt(LocalDateTime.now())
                .task(task)
                .build();
        task.getFiles().add(file);
        return taskRepository.save(task);
    }

    public Task updateTaskPriority(Long taskId, Priority newPriority) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setPriority(newPriority);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public List<Task> getTasksByPriorityAndStatus(Priority priority, TaskStatus status) {
        return taskRepository.findByPriorityAndStatus(priority, status);
    }

    public List<Task> getAllTasksSortedByPriority(String order) {
        List<Task> tasks = taskRepository.findAll();
        if ("desc".equalsIgnoreCase(order)) {
            tasks.sort(Comparator.comparing(Task::getPriority).reversed());
        } else {
            tasks.sort(Comparator.comparing(Task::getPriority));
        }
        return tasks;
    }


}

