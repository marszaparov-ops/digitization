package com.archive.digitization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.io.IOException;

@Service
public class FileWatcherService {

    @Autowired
    private DocumentRepository documentRepository;

    // ... (код инициализации WatchService)

    private void processEvent(WatchEvent<?> event, Path dir) {
        WatchEvent.Kind<?> kind = event.kind();

        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            // Вот здесь мы получаем правильный путь к файлу
            Path filePath = (Path) event.context();
            String fileName = filePath.getFileName().toString();

            Document doc = new Document();
            doc.setTitle("Автоматическая загрузка");
            doc.setFileName(fileName); // Теперь переменная определена выше
            doc.setFileType(getFileExtension(fileName));

            documentRepository.save(doc);
            System.out.println("Документ сохранен: " + fileName);
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) return "Unknown";
        return fileName.substring(lastIndexOf + 1).toUpperCase();
    }
}