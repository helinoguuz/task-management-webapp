package com.example.demo.controller;

import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserStoryRepository;
import com.example.demo.model.Task;
import com.example.demo.model.Priority;
import com.example.demo.service.NotificationService;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.util.List;
import java.util.Comparator;
import java.util.Optional;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @Autowired
    public TaskController(TaskService taskService, UserRepository userRepository, NotificationService notificationService) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/tasks")
    public String showTaskPage(@RequestParam(required = false) Priority priority,
                               @RequestParam(required = false) TaskStatus status,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String order,
                               Model model) {

        List<Task> tasks;

        if (priority != null && status != null) {
            tasks = taskService.getTasksByPriorityAndStatus(priority, status);
        } else if (priority != null) {
            tasks = taskService.getTasksByPriority(priority);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status);
        } else {
            tasks = taskService.getAllTasks();
        }

        if ("priority".equals(sort)) {
            tasks.sort(Comparator.comparing(Task::getPriority));
            if ("desc".equalsIgnoreCase(order)) {
                tasks.sort(Comparator.comparing(Task::getPriority).reversed());
            }
        } else if ("createdAt".equals(sort)) {
            tasks.sort(Comparator.comparing(Task::getCreatedAt));
            if ("desc".equalsIgnoreCase(order)) {
                tasks.sort(Comparator.comparing(Task::getCreatedAt).reversed());
            }
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedOrder", order);
        model.addAttribute("allUsers", userRepository.findAll());

        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("allUserStories", userStoryRepository.findAll());
        return "task-form";
    }
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("allUserStories", userStoryRepository.findAll());
        return "task-form";
    }

    @GetMapping("/download/{storedFileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storedFileName) throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads", storedFileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String originalFileName = storedFileName.substring(storedFileName.indexOf('_') + 1);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(resource);
    }

    @PostMapping("/tasks")
    public String createTask(@ModelAttribute Task task, Model model) {
        try {
            if (task.getAssignedUser() != null && task.getAssignedUser().getId() != null) {
                User user = userRepository.findById(task.getAssignedUser().getId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                task.setAssignedUser(user);
            }

            taskService.createTask(
                    task.getTitle(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getAssignedUser(),
                    task.getUserStoryId()
            );

            if (task.getAssignedUser() != null) {
                notificationService.send(
                        task.getAssignedUser().getEmail(),
                        "You were assigned to task: " + task.getTitle()
                );
            }

            return "redirect:/tasks";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());

            model.addAttribute("task", task);
            model.addAttribute("allUsers", userRepository.findAll());
            model.addAttribute("allUserStories", userStoryRepository.findAll());

            return "task-form";
        }
    }

    @PostMapping("/api/tasks")
    @ResponseBody
    public ResponseEntity<Task> createTaskFromJson(@RequestBody Task task) {
        Task savedTask = taskService.createTask(
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getAssignedUser(),
                task.getUserStoryId()
        );
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @PostMapping("/tasks/{taskId}/upload")
    public String uploadFileToTask(@PathVariable Long taskId,
                                   @RequestParam("file") MultipartFile file) {
        taskService.uploadFileToTask(taskId, file);
        return "redirect:/tasks";
    }

    @PatchMapping("/tasks/{taskId}/status")
    @ResponseBody
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) {
        Task updatedTask = taskService.updateTaskStatus(taskId, status);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @PatchMapping("/tasks/{taskId}/assign")
    public ResponseEntity<Task> updateAssignedUser(@PathVariable Long taskId, @RequestParam Long userId) {
        Task updatedTask = taskService.updateAssignedUser(taskId, userId);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @PostMapping("/tasks/{id}/status")
    public String updateTaskStatusForm(@PathVariable Long id, @RequestParam TaskStatus status) {
        taskService.updateTaskStatus(id, status);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/assign")
    public String assignUser(@PathVariable Long id, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        taskService.updateAssignedUser(id, userId);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/priority")
    public String updatePriority(@PathVariable Long id, @RequestParam Priority priority) {
        taskService.updateTaskPriority(id, priority);
        return "redirect:/tasks";
    }

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/tasks/{id}")
    public String showTaskDetail(@PathVariable Long id, Model model) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            model.addAttribute("task", taskOptional.get());
            return "task-detail";
        } else {
            return "redirect:/tasks"; // Not found → zurück zur Übersicht
        }
    }

}
