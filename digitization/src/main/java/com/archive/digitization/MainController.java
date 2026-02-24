package com.archive.digitization;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String home() {
        return "<h1>Добро пожаловать в систему архивации!</h1>" +
                "<p>Вы успешно вошли как администратор.</p>";
    }
}