package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AccountService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AccountService accountService;

    public ProfileController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Profil sayfasını göster
    @GetMapping
    public String showProfile(Model model, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) {
            return "redirect:/login";
        }

        User user = accountService.getUserByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }
        System.out.println("Profil fotoğraf yolu: " + user.getProfilePictureUrl());
        model.addAttribute("user", user);
        return "profile"; // profile.html
    }

    // Profil fotoğrafı yükleme
    @PostMapping("/upload-photo")
    public String uploadProfilePicture(@RequestParam("profilePictureFile") MultipartFile file,
                                       HttpSession session,
                                       Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null || file.isEmpty()) {
            model.addAttribute("error", "Datei fehlt oder nicht angemeldet.");
            return "redirect:/profile";
        }
        System.out.println("File Name: " + file.getOriginalFilename());
        System.out.println("Typ von der Datei: " + file.getContentType());      //Ich habe dies geschrieben, um zu sehen, ob es richtig funktioniert.
        System.out.println("Size: " + file.getSize());


        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> File saved at: " + filePath.toAbsolutePath());

            User user = accountService.getUserByEmail(email);
            user.setProfilePictureUrl("/uploads/" + filename);
            accountService.saveUser(user);

            return "redirect:/profile";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Fehler beim Hochladen.");
            return "redirect:/profile";
        }
    }
    @PostMapping("/change-password")
    public String changePassword(HttpSession session,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        boolean success = accountService.changePassword(email, oldPassword, newPassword);

        if (success) {
            model.addAttribute("message", "Passwort wurde erfolgreich geändert");
        } else {
            model.addAttribute("error", "Altes Passwort ist falsch");
        }

        return "profile";
    }

    // Bio güncelleme
    @PostMapping("/update-bio")
    public String updateBio(@RequestParam("bio") String bio,
                            HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) {
            return "redirect:/login";
        }

        User user = accountService.getUserByEmail(email);
        user.setBio(bio);
        accountService.saveUser(user);

        return "redirect:/profile";
    }

    @GetMapping("/test-upload-write")
    @ResponseBody
    public String testWrite() {
        try {
            Path testPath = Paths.get("uploads/test-check.txt");
            Files.createDirectories(testPath.getParent());
            Files.writeString(testPath, "Upload erfolgreich");
            return "Testdatei geschrieben: " + testPath.toAbsolutePath();
        } catch (IOException e) {
            return "Schreiben fehlgeschlagen: " + e.getMessage();
        }
    }
    @PostConstruct
    public void printUploadPath() {
        System.out.println(">>> Arbeitsverzeichnis (user.dir): " + System.getProperty("user.dir"));
    }


    @GetMapping("/debug")
    @ResponseBody
    public String debugCheck() {
        return ">>> Aktuelles Arbeitsverzeichnis: " + System.getProperty("user.dir");
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
        }
    }

    @GetMapping("/view/{userId}")
    public String viewOtherProfile(@PathVariable Long userId, HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        User currentUser = accountService.getUserByEmail(email);
        if (currentUser == null) return "redirect:/login";

        // Eğer kendi profilini görüntülemeye çalışıyorsa yönlendirir
        if (currentUser.getId().equals(userId)) {
            return "redirect:/profile";
        }

        User targetUser = accountService.getUserById(userId);
        if (targetUser == null) {
            model.addAttribute("error", "Nutzer nicht gefunden.");
            return "error"; // bir hata sayfan varsa
        }

        model.addAttribute("user", targetUser);
        return "view-profile"; // sadece görüntüleme için sayfa
    }




}
