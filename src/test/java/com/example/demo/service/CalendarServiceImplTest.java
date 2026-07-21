package com.example.demo.service;

import com.example.demo.model.CalendarEntry;
import com.example.demo.repository.CalendarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarServiceImplTest {

    private CalendarRepository calendarRepository;
    private CalendarServiceImpl calendarService;

    private CalendarEntry entry;

    @BeforeEach
    void setUp() {
        calendarRepository = mock(CalendarRepository.class);
        calendarService = new CalendarServiceImpl(calendarRepository);


        entry = new CalendarEntry();
        entry.setId(1L);
        entry.setTitle("Meeting");
        entry.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 0));
        entry.setEndTime(LocalDateTime.of(2025, 7, 20, 11, 0));
    }

    @Test
    void testGetAllEntries_shouldReturnList() {
        when(calendarRepository.findAll()).thenReturn(List.of(entry));

        List<CalendarEntry> result = calendarService.getAllEntries();

        assertEquals(1, result.size());
        assertEquals("Meeting", result.get(0).getTitle());
    }

    @Test
    void testSaveEntry_shouldSaveAndReturn() {
        when(calendarRepository.save(entry)).thenReturn(entry);

        CalendarEntry result = calendarService.saveEntry(entry);

        assertNotNull(result);
        assertEquals("Meeting", result.getTitle());
    }

    @Test
    void testDeleteEntry_shouldCallRepository() {
        doNothing().when(calendarRepository).deleteById(1L);

        calendarService.deleteEntry(1L);

        verify(calendarRepository, times(1)).deleteById(1L);
    }
}
