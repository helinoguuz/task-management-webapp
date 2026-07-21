package com.example.demo.controller;

import com.example.demo.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class VerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String token,
                              Model model) {
        boolean success = emailVerificationService.verifyEmail(email, token);

        if (success) {
            model.addAttribute("message", "Email successfully verified!");
        } else {
            model.addAttribute("message", "Invalid verification link.");
        }

        return "verification-result";
    }
}
