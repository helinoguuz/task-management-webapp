package com.example.demo.controller;

import com.example.demo.model.CalendarEntry;
import com.example.demo.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar2")
@RequiredArgsConstructor
public class CalendarRestController {

    private final CalendarService calendarService;

    @GetMapping
    public ResponseEntity<List<CalendarEntry>> getAll() {
        return ResponseEntity.ok(calendarService.getAllEntries());
    }

    @PostMapping
    public ResponseEntity<CalendarEntry> create(@RequestBody CalendarEntry entry) {
        CalendarEntry saved = calendarService.saveEntry(entry);
        return ResponseEntity.status(201).body(saved);
    }


}
