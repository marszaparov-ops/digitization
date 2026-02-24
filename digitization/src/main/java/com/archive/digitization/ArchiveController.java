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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Controller
public class ArchiveController {

    @Autowired
    private DocumentRepository documentRepository;

    // Папка создастся автоматически внутри твоего проекта
    private final String UPLOAD_DIR = "archive-storage/";

    @GetMapping("/archive")
    public String listDocuments(Model model,
                                @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10; // Сколько строк выводить на одной странице
        Page<Document> docPage = documentRepository.findAll(PageRequest.of(page, pageSize));

        model.addAttribute("documents", docPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", docPage.getTotalPages());

        return "archive";
    }

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
                    Files.createDirectories(uploadPath);
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                Document doc = new Document();
                doc.setTitle((title == null || title.isEmpty()) ? file.getOriginalFilename() : title);
                doc.setDocNumber(docNumber);
                doc.setCategory(category);
                doc.setDescription(description);
                doc.setFileName(fileName);
                doc.setFileType(file.getContentType());

                documentRepository.save(doc);
            }
        } catch (IOException e) {
            System.err.println("ОШИБКА: " + e.getMessage());
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
                    .contentType(org.springframework.http.MediaType.parseMediaType(doc.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/archive/delete/{id}")
    public String deleteDocument(@PathVariable Long id, Principal principal) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc != null) {
            // Записываем КТО и ЧТО удалил перед самим удалением
            String logInfo = "Удалил документ: " + doc.getTitle() + " (№ " + doc.getDocNumber() + ")";
            actionLogRepository.save(new ActionLog(principal.getName(), "УДАЛЕНИЕ", logInfo));

            documentRepository.delete(doc);
        }
        return "redirect:/archive";
    }

    @GetMapping("/archive/scan-refresh")
    public String scanRefresh() {
        return "redirect:/archive";
    }
}
@Autowired
private ActionLogRepository actionLogRepository; // Теперь контроллер видит таблицу логов