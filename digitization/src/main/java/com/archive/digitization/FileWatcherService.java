package com.archive.digitization;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileWatcherService {

    @Autowired
    private DocumentRepository documentRepository;

    private final String WATCH_DIR = "E:/Work/archive/archive-storage/";

    @PostConstruct
    public void startWatching() {
        // Запускаем наблюдение в отдельном потоке, чтобы не тормозить программу
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path path = Paths.get(WATCH_DIR);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path fileName = (Path) event.context();

                        // Когда сканер создал файл, записываем его в базу
                        saveToDatabase(fileName.toString());
                    }
                    key.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveToDatabase(String fileName) {
        Document doc = new Document();
        doc.setTitle("Скан из BookTEK: " + fileName);
        doc.setFileName(fileName);
        doc.setFileType("image/jpeg"); // Или определять автоматически
        documentRepository.save(doc);
        System.out.println("Файл отсканирован и добавлен в базу: " + fileName);
    }
}