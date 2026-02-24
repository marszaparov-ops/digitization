package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ArchiveController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/archive")
    public String archivePage() {
        return "archive";
    }

    // ВАЖНО: название метода и возвращаемое имя файла
    @GetMapping("/archive/users")
    public String usersPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users-list"; // Ищет именно users-list.html
    }

    @PostMapping("/archive/users/add")
    public String addUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/archive/users";
    }

    @GetMapping("/fix-admin")
    @ResponseBody
    public String fixAdmin() {
        User admin = userRepository.findByUsername("admin").orElse(new User());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);
        userRepository.save(admin);
        return "Admin fixed! Login: admin / Pass: admin";
    }
    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/archive/documents")
    public String documentsPage(Model model) {
        model.addAttribute("documents", documentRepository.findAll());
        return "documents-list";
    }

    @PostMapping("/archive/documents/add")
    public String addDocument(@ModelAttribute Document document) {
        documentRepository.save(document);
        return "redirect:/archive/documents";
    }
}