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

    private final String UPLOAD_DIR = "E:/Work/archive/archive-storage/";

    @GetMapping("/archive")
    public String listDocuments(Model model) {
        List<Document> docs = documentRepository.findAll();
        model.addAttribute("documents", docs);
        return "archive";
    }

    @PostMapping("/archive/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("title") String title) {
        try {
            if (!file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                Document doc = new Document();
                doc.setTitle(title);
                doc.setFileName(fileName);
                doc.setFileType(file.getContentType());
                documentRepository.save(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/archive";
    }

    // МЕТОД ДЛЯ СКАЧИВАНИЯ
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

    // МЕТОД ДЛЯ УДАЛЕНИЯ
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
}