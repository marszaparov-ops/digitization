package com.archive.digitization;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Кто сделал
    private String action;   // Что сделал (Загрузил/Удалил)
    private String details;  // Название файла или доп. инфо
    private LocalDateTime timestamp; // Время

    // Конструктор по умолчанию (нужен для Hibernate)
    public ActionLog() {}

    // Удобный конструктор для создания лога
    public ActionLog(String username, String action, String details) {
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Вспомогательный метод для красивого вывода даты в HTML
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }
}