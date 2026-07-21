package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.Priority;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUserId(Long userId);
    List<Task> findByProjectId(Long projectId);
    List<Task> findByProject(Project project);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(Priority priority);
    List<Task> findByPriorityAndStatus(Priority priority, TaskStatus status);
    List<Task> findAll();
}
