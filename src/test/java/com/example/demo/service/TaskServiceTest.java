package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserStoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private TaskService taskService;
    private NotificationService notificationService;
    private UserStoryRepository userStoryRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        notificationService = mock(NotificationService.class);
        taskRepository = mock(TaskRepository.class);
        userStoryRepository = mock(UserStoryRepository.class);

        taskService = new TaskService(userRepository, notificationService, taskRepository,userStoryRepository);
    }

    @Test
    void testGetTaskById_whenTaskExists_returnsTask() {
        Task task = new Task();
        task.setId(123L);

        when(taskRepository.findById(123L)).thenReturn(Optional.of(task));

        Task result = taskService.getTask("123");

        assertNotNull(result);
        assertEquals(123L, result.getId());
    }

    @Test
    void testGetTaskById_whenTaskDoesNotExist_returnsNull() {
        when(taskRepository.findById(123L)).thenReturn(Optional.empty());

        Task result = taskService.getTask("123");

        assertNull(result);
    }

    @Test
    void testGetTask_whenInvalidId_returnsNull() {
        Task result = taskService.getTask("notANumber");

        assertNull(result);
    }
}

