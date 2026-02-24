package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Controller
public class ArchiveController {

    @Autowired private DocumentRepository documentRepository;
    @Autowired private ActionLogRepository actionLogRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // РАЗДЕЛ: ДОКУМЕНТЫ
    @GetMapping("/archive")
    public String listDocuments(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Document> docPage = documentRepository.findAll(PageRequest.of(page, 10, Sort.by("id").descending()));
        model.addAttribute("documents", docPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", docPage.getTotalPages());
        model.addAttribute("section", "documents");
        return "archive";
    }

    // РАЗДЕЛ: АРХИВ (ЛОГИ)
    @GetMapping("/archive/history")
    public String viewHistory(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<ActionLog> logPage = actionLogRepository.findAll(PageRequest.of(page, 15, Sort.by("id").descending()));
        model.addAttribute("logs", logPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("section", "history");
        return "archive";
    }

    // РАЗДЕЛ: ПОЛЬЗОВАТЕЛИ
    @GetMapping("/archive/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("section", "users");
        return "archive";
    }

    // ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ
    @PostMapping("/archive/users/add")
    public String addUser(@RequestParam String username, @RequestParam String password, @RequestParam String role, Principal principal) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setEnabled(true);
        userRepository.save(newUser);
        actionLogRepository.save(new ActionLog(principal.getName(), "СОЗДАНИЕ", "Создан юзер: " + username));
        return "redirect:/archive/users";
    }
    @GetMapping("/fix-admin")
    @ResponseBody
    public String fixAdmin() {
        User user = userRepository.findByUsername("admin").orElse(new User());
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin")); // Это создаст ПРАВИЛЬНЫЙ хеш
        user.setRole("ROLE_ADMIN");
        user.setEnabled(true);
        userRepository.save(user);
        return "Пароль для admin успешно обновлен на 'admin'!";
    }
    @GetMapping("/setup-admin")
    @ResponseBody
    public String setupAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin")); // Пароль будет admin
        admin.setRole("ROLE_ADMIN"); // Добавляем префикс ROLE_ обязательно
        admin.setEnabled(true);
        userRepository.save(admin);
        return "Аккаунт admin/admin создан и зашифрован!";
    }
}