package com.example.demo.service;

import com.example.demo.model.CalendarEntry;
import com.example.demo.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    public CalendarServiceImpl(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }


    @Override
    public List<CalendarEntry> getAllEntries() {
        return calendarRepository.findAll();
    }

    @Override
    public CalendarEntry saveEntry(CalendarEntry entry) {
        return calendarRepository.save(entry);
    }

    @Override
    public List<CalendarEntry> getEntriesForUser(Long userId) {
        return List.of();
    }

    @Override
    public List<CalendarEntry> getEntriesBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public CalendarEntry createEntry(CalendarEntry entry) {
        return null;
    }

    @Override
    public CalendarEntry updateEntry(Long id, CalendarEntry entry) {
        return null;
    }

    @Override
    public void deleteEntry(Long id) {
        calendarRepository.deleteById(id);
    }
}
