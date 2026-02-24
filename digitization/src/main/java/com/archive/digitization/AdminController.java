package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // 1. Показ списка всех пользователей
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("allUsers", users);
        return "users-list";
    }

    // 2. Добавление нового пользователя
    @PostMapping("/admin/users/add")
    public String addUser(User newUser) {
        userRepository.save(newUser);
        return "redirect:/admin/users";
    }

    // 3. Удаление пользователя по ID
    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}