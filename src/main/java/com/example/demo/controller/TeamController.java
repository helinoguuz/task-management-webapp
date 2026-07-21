package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TeamController {

    private final AccountService accountService;

    @Autowired
    public TeamController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/team")
    public String showTeam(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        List<User> users = accountService.getAllUsers();
        model.addAttribute("users", users);
        return "team";
    }
}
