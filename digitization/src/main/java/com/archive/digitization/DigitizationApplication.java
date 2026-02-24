package com.archive.digitization;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DigitizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitizationApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository repository) {
        return args -> {
            // Создаем админа, если в базе еще никого нет
            if (repository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("123");
                admin.setRole("ADMIN");
                repository.save(admin);
                System.out.println(">>> Пользователь admin успешно добавлен в базу!");
            }
        };
    }
}