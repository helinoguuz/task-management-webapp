package com.example.demo.controller;

import com.example.demo.model.CalendarEntry;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService, TaskRepository taskRepository) {
        this.calendarService = calendarService;
        this.taskRepository = taskRepository;
    }


    @GetMapping
    public List<CalendarEntry> getAllEntries() {
        return calendarService.getAllEntries();
    }


    @PostMapping
    public CalendarEntry createEntry(@RequestBody CalendarEntry entry) {
        return calendarService.saveEntry(entry);
    }


    @DeleteMapping("/{id}")
    public void deleteEntry(@PathVariable Long id) {
        calendarService.deleteEntry(id);
    }
    private final TaskRepository taskRepository;

    @GetMapping("/api/calendar")
    public List<Map<String, Object>> getCalendarTasks() {
        List<Map<String, Object>> events = new ArrayList<>();

        List<Task> tasks = taskRepository.findAll();

        for (Task task : tasks) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", task.getTitle());
            event.put("start", task.getCreatedAt()); // ISO formatta string döner
            events.add(event);
        }

        return events;
    }

}
