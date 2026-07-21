package com.example.demo.controller;

import com.example.demo.model.UserStory;
import com.example.demo.service.UserStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userstories")
@RequiredArgsConstructor
public class UserStoryRestController {

    private final UserStoryService userStoryService;

    @GetMapping
    public ResponseEntity<List<UserStory>> getAll() {
        return ResponseEntity.ok(userStoryService.getAllStories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStory> getById(@PathVariable Long id) {
        return userStoryService.getStoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserStory> create(@RequestBody UserStory story) {
        UserStory saved = userStoryService.saveStory(story);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStory> update(@PathVariable Long id, @RequestBody UserStory story) {
        return userStoryService.getStoryById(id)
                .map(existing -> {
                    story.setId(id);
                    return ResponseEntity.ok(userStoryService.saveStory(story));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userStoryService.getStoryById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userStoryService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }
}
