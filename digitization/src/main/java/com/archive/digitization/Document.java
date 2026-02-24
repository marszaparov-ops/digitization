package com.archive.digitization;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String docNumber;
    private String category;
    private String fileName;
    private String fileType;

    private String uploadedBy; // Имя пользователя
    private LocalDateTime uploadDate = LocalDateTime.now(); // Дата загрузки

    // Геттеры и сеттеры (создай их в IDEA через Alt+Insert -> Getter and Setter)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDocNumber() { return docNumber; }
    public void setDocNumber(String docNumber) { this.docNumber = docNumber; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}