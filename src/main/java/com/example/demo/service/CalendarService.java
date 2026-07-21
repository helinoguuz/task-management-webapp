package com.example.demo.service;

import com.example.demo.model.CalendarEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {
    List<CalendarEntry> getEntriesForUser(Long userId);
    List<CalendarEntry> getEntriesBetween(LocalDateTime start, LocalDateTime end);
    CalendarEntry createEntry(CalendarEntry entry);
    CalendarEntry updateEntry(Long id, CalendarEntry entry);
    void deleteEntry(Long id);
    List<CalendarEntry> getAllEntries();
    CalendarEntry saveEntry(CalendarEntry entry);
}
