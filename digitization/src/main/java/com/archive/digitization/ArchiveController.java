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

    // Страница логина
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Главная страница
    @GetMapping("/archive")
    public String archivePage(Model model) {
        // Достаем всех пользователей, чтобы таблица на главной не была пустой
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "archive";
    }

    // Список пользователей (только для админа)
    @GetMapping("/archive/users")
    public String usersPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    // Сохранение нового пользователя
    @PostMapping("/archive/users/add")
    public String addUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/archive/users";
    }

    // Временный фикс, если не пускает
    @GetMapping("/fix-admin")
    @ResponseBody
    public String fixAdmin() {
        User admin = userRepository.findByUsername("admin").orElse(new User());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);
        userRepository.save(admin);
        return "Аккаунт admin/admin обновлен!";
    }
}