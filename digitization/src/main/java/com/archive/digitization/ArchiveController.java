package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class ArchiveController {

    @Autowired private DocumentRepository documentRepository;
    @Autowired private ActionLogRepository actionLogRepository;
    @Autowired private UserRepository userRepository; // Добавь этот репозиторий

    // 1. РАЗДЕЛ: ДОКУМЕНТЫ
    @GetMapping("/archive")
    public String listDocuments(Model model, @RequestParam(defaultValue = "0") int page) {
        var docPage = documentRepository.findAll(PageRequest.of(page, 10, Sort.by("id").descending()));
        model.addAttribute("documents", docPage.getContent());
        model.addAttribute("totalPages", docPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("section", "documents"); // Важно для HTML
        return "archive";
    }

    // 2. РАЗДЕЛ: АРХИВ (ЛОГИ)
    @GetMapping("/archive/history")
    public String viewHistory(Model model, @RequestParam(defaultValue = "0") int page) {
        var logPage = actionLogRepository.findAll(PageRequest.of(page, 15, Sort.by("id").descending()));
        model.addAttribute("logs", logPage.getContent());
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("section", "history"); // Важно для HTML
        return "archive";
    }

    // 3. РАЗДЕЛ: ПОЛЬЗОВАТЕЛИ
    @GetMapping("/archive/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("section", "users"); // Важно для HTML
        return "archive";
    }
}