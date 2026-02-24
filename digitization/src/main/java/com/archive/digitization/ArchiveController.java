package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Controller
public class ArchiveController {

    @Autowired
    private DocumentRepository documentRepository;

    // Используем один путь для всех файлов
    private final String UPLOAD_DIR = "E:/Work/archive/archive-storage/";

    @GetMapping("/archive")
    public String listDocuments(Model model) {
        List<Document> docs = documentRepository.findAll();
        model.addAttribute("documents", docs);
        return "archive";
    }

    // ИСПРАВЛЕННЫЙ МЕТОД: принимает файл и данные из формы
    @PostMapping("/archive/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "docNumber", required = false) String docNumber,
                             @RequestParam(value = "category", required = false) String category,
                             @RequestParam(value = "description", required = false) String description) {
        try {
            if (!file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Сохраняем в базу данных со всеми полями
                Document doc = new Document();
                doc.setTitle(title != null ? title : file.getOriginalFilename());
                doc.setDocNumber(docNumber);
                doc.setCategory(category);
                doc.setDescription(description);
                doc.setFileName(fileName);
                doc.setFileType(file.getContentType());

                documentRepository.save(doc);
                System.out.println("Файл сохранен и записан в базу: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/archive";
    }

    @GetMapping("/archive/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Document doc = documentRepository.findById(id).orElseThrow();
            Path path = Paths.get(UPLOAD_DIR).resolve(doc.getFileName());
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/archive/delete/{id}")
    public String deleteDocument(@PathVariable Long id) {
        try {
            Document doc = documentRepository.findById(id).orElse(null);
            if (doc != null) {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR).resolve(doc.getFileName()));
                documentRepository.delete(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/archive";
    }

    // Метод для кнопки сканера (чтобы не было 404)
    @GetMapping("/archive/scan-refresh")
    public String scanRefresh() {
        System.out.println("Запрос на обновление данных со сканера...");
        return "redirect:/archive";
    }
}