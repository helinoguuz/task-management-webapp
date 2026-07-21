package com.example.demo.service;

import com.example.demo.model.Subtask;
import com.example.demo.model.Task;
import com.example.demo.repository.SubtaskRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public SubtaskService(SubtaskRepository subtaskRepository, TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
    }

    public void createSubtask(Subtask subtask) {
        subtaskRepository.save(subtask);
    }

    public Optional<Subtask> getSubtask(String id) {
        return subtaskRepository.findById(Long.parseLong(id));
    }

    public List<Subtask> getSubtasksByTask(String taskId) {
        Optional<Task> task = taskRepository.findById(Long.parseLong(taskId));
        return task.map(subtaskRepository::findByTask).orElseGet(List::of);
    }

    public List<Subtask> getAllSubtasks() {
        return subtaskRepository.findAll();
    }

    public void deleteSubtask(String id) {
        subtaskRepository.deleteById(Long.parseLong(id));
    }

    public void updateSubtask(Subtask updatedSubtask) {
        subtaskRepository.save(updatedSubtask);
    }
}

