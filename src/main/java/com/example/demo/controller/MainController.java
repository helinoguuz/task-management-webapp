package com.example.demo.controller;

import com.example.demo.dao.IPasswordHasher;
import com.example.demo.service.AccountService;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.service.TaskService;
import com.example.demo.model.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.springframework.web.bind.annotation.ResponseBody;



import java.util.List;

@org.springframework.stereotype.Controller
public class MainController {

    private final AccountService accountService;
    private final IPasswordHasher passwordHasher;

    public MainController(AccountService accountService, IPasswordHasher passwordHasher){
        this.accountService = accountService;
        this.passwordHasher = passwordHasher;
    }
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        if (accountService.emailExists(email)) {
            model.addAttribute("error", "Diese E-Mail ist bereits registriert.");
            return "register";
        }

        boolean success = accountService.registerUser(name, email, password);

        if (success) {
            session.setAttribute("loggedInUser", email);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Registrierung fehlgeschlagen.");
            return "register";
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        boolean success = accountService.login(email, password);

        if (success) {
            session.setAttribute("loggedInUser", email);
            return "redirect:/index";
        } else {
            model.addAttribute("error", "E-Mail oder Passwort falsch!");
            return "login";
        }
    }
    @Autowired
    private TaskService taskService;

    @GetMapping("/board")
    public String showBoard(Model model) {
        model.addAttribute("backlogTasks", taskService.getTasksByStatus(TaskStatus.NEW));               // Backlog
        model.addAttribute("todoTasks", taskService.getTasksByStatus(TaskStatus.ON_HOLD));              // To Do
        model.addAttribute("inProgressTasks", taskService.getTasksByStatus(TaskStatus.IN_PROGRESS));    // In Progress
        model.addAttribute("doneTasks", taskService.getTasksByStatus(TaskStatus.COMPLETED));            // Done
        return "board";
    }


    @PostMapping("/internal/tasks/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        taskService.updateTaskStatus(id, TaskStatus.valueOf(newStatus));
        return ResponseEntity.ok().build();
    }




}
