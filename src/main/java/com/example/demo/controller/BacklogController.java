package com.example.demo.controller;

import com.example.demo.model.UserStory;
import com.example.demo.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BacklogController {

    private final UserStoryRepository userStoryRepository;

    @GetMapping("/backlog")
    public String showBacklog(Model model) {
        model.addAttribute("userStories", userStoryRepository.findAll());
        model.addAttribute("newUserStory", new UserStory());
        model.addAttribute("priorities", com.example.demo.model.Priority.values()); // 🔥 BURASI EKLENDİ
        return "backlog";
    }

    @PostMapping("/backlog")
    public String addUserStory(@ModelAttribute UserStory newUserStory) {
        userStoryRepository.save(newUserStory);
        return "redirect:/backlog";
    }

    @GetMapping("/backlog/delete/{id}")
    public String deleteUserStory(@PathVariable Long id) {
        userStoryRepository.deleteById(id);
        return "redirect:/backlog";
    }

    @GetMapping("/backlog/edit/{id}")
    public String editUserStoryForm(@PathVariable Long id, Model model) {
        UserStory story = userStoryRepository.findById(id).orElseThrow();
        model.addAttribute("userStory", story);
        return "edit_backlog";
    }

    @PostMapping("/backlog/edit/{id}")
    public String updateUserStory(@PathVariable Long id, @ModelAttribute UserStory userStory) {
        userStory.setId(id);
        userStoryRepository.save(userStory);
        return "redirect:/backlog";
    }
}
