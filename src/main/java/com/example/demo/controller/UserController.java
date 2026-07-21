package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final AccountService accountService;

    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    // US-016: Profilini görüntüle
    @GetMapping("/profile")
    public String viewOwnProfile(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) {
            return "redirect:/login";
        }

        User user = accountService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "profile";
    }

    // US-016: Profilini güncelle
    @PostMapping("/update")
    public String updateOwnProfile(@RequestParam String name,
                                   @RequestParam String email,
                                   HttpSession session,
                                   Model model) {

        String loggedInEmail = (String) session.getAttribute("loggedInUser");
        if (loggedInEmail == null) {
            return "redirect:/login";
        }

        boolean success = accountService.updateProfile(loggedInEmail, name, email);
        if (success) {
            session.setAttribute("loggedInUser", email); // e-mail değiştiyse güncelle
            return "redirect:/user/profile";
        } else {
            model.addAttribute("error", "Aktualisierung fehlgeschlagen.");
            return "profile";
        }
    }

    // US-015: Diğer kullanıcıları listele (örnek)
    @GetMapping("/all")
    public String viewAllProfiles(Model model) {
        List<User> users = accountService.getAllUsers();
        model.addAttribute("users", users);
        return "user-list";
    }

    // US-017: Admin rol güncellemesi
    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam String targetEmail,
                                 @RequestParam UserRole role,
                                 HttpSession session,
                                 Model model) {

        String loggedInEmail = (String) session.getAttribute("loggedInUser");
        User admin = accountService.getUserByEmail(loggedInEmail);

        if (admin == null || admin.getRole() != UserRole.ADMIN) {
            model.addAttribute("error", "Nur Admins dürfen Rollen ändern.");
            return "redirect:/";
        }

        accountService.updateUserRole(targetEmail, role);
        return "redirect:/user/all";
    }

}

