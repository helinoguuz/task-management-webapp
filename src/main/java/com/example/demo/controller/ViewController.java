package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String showloginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showregisterPage() {
        return "register";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
    @GetMapping("/")
    public String showPage() {
        return "page";
    }

    @GetMapping("/calendar")
    public String calendarPage() {
        return "calendar";
    }

}
