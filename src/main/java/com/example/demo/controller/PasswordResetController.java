package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/reset-password-request")
    public String showResetPasswordRequestForm() {
        return "reset-password-request";
    }

    @PostMapping("/reset-password-request")
    public String handleResetPasswordRequest(@RequestParam String email, Model model) {
        User user = accountService.findUserByEmail(email);
        if (user != null) {
            passwordResetService.sendPasswordResetEmail(user);
        }

        model.addAttribute("message", "If an account with that email exists, a reset link has been sent.");
        return "reset-password-request-result";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String email, @RequestParam String token, Model model) {
        boolean valid = passwordResetService.verifyResetToken(email, token);
        if (valid) {
            model.addAttribute("email", email);
            model.addAttribute("token", token);
            return "reset-password-form";
        } else {
            model.addAttribute("message", "Invalid or expired reset link.");
            return "reset-password-error";
        }
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email,
                                      @RequestParam String token,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        boolean valid = passwordResetService.verifyResetToken(email, token);
        if (!valid) {
            model.addAttribute("message", "Invalid or expired reset link.");
            return "reset-password-error";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match.");
            model.addAttribute("email", email);
            model.addAttribute("token", token);
            return "reset-password-form";
        }

        accountService.updatePassword(email, newPassword);
        passwordResetService.clearResetToken(email);
        model.addAttribute("message", "Password successfully reset. You can now login.");
        return "reset-password-success";
    }

}
