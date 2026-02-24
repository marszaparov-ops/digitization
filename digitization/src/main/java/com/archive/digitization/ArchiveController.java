package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchiveController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    // Главная страница: http://localhost:8080/archive
    @GetMapping("/archive")
    public String mainPage() {
        return "archive";
    }

    // Пользователи: http://localhost:8080/users
    @GetMapping("/users")
    public String usersList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users-list";
    }

    // Документы: http://localhost:8080/documents
    @GetMapping("/documents")
    public String documentsList(Model model) {
        model.addAttribute("documents", documentRepository.findAll());
        return "documents-list";
    }
}