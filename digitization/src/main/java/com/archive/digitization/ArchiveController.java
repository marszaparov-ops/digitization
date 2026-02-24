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
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Создаем папки, если их нет
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                Document doc = new Document();
                // Если заголовок пустой, берем имя файла
                doc.setTitle((title == null || title.isEmpty()) ? file.getOriginalFilename() : title);
                doc.setDocNumber(docNumber);
                doc.setCategory(category);
                doc.setDescription(description);
                doc.setFileName(fileName);
                doc.setFileType(file.getContentType());

                documentRepository.save(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Если ошибка — выводим её в консоль, чтобы мы видели причину
            System.out.println("ОШИБКА ЗАГРУЗКИ: " + e.getMessage());
        }

        // Возвращаемся строго на главную страницу архива
        return "redirect:/archive";
    }

    @GetMapping("/archive/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Document doc = documentRepository.findById(id).orElseThrow();
            Path path = Paths.get(UPLOAD_DIR).resolve(doc.getFileName());
            Resource resource = new UrlResource(path.toUri());

            // Определяем тип файла (Content-Type)
            String contentType = doc.getFileType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    // "inline" заставляет браузер открыть файл, а не качать его
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
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