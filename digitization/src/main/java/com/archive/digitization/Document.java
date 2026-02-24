package com.archive.digitization;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;      // Название (например, "Приказ №5")
    private String fileName;   // Имя файла на диске
    private String fileType;   // Формат (pdf, docx, jpg)
    private LocalDateTime uploadDate; // Дата загрузки

    public Document() {
        this.uploadDate = LocalDateTime.now(); // Автоматически ставим текущее время
    }

    // Геттеры и сеттеры (без них Spring не сможет работать с данными)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public LocalDateTime getUploadDate() { return uploadDate; }
}