package com.hammad.file.service.service;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
public class FileCreationService {

    private static final int TOTAL_FILES = 1_000_000; // Total number of files to create
    private static final int THREADS = 10; // Number of threads to use
    private static final int FILES_PER_THREAD = TOTAL_FILES / THREADS; // Files per thread

    public static void createFiles(String path, String namePrefix, int totalFiles) {

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            int start = i * FILES_PER_THREAD;
            int end = (i + 1) * FILES_PER_THREAD;
            tasks.add(createFileTask(start, end));
        }

        tasks.forEach(executor::submit);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static Runnable createFileTask(final int start, final int end) {
        return () -> {
            for (int i = start; i < end; i++) {
                try {
                    Files.createFile(Paths.get("C:/files/file" + i + ".txt"));
                } catch (IOException e) {
                    log.atInfo().log("Error creating file: {}", e.getMessage());
                }
            }
        };
    }
}