package com.example.demo.repository;

import com.example.demo.model.CalendarEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntry, Long> {
    // Özel sorgular istersek buraya ekleyebiliriz
}