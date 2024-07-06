package com.hammad.file.service.service;

import com.hammad.file.service.exception.ResourceAccessDeniedException;
import com.hammad.file.service.exception.ResourceNotFoundException;
import com.hammad.file.service.model.FileInfo;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

@Service
@Log4j2
public class FileInfoService {

    public Flux<Object> listFiles(String dirPath, boolean recursive) {
        if (!isValidPath(dirPath)) {
            return Flux.empty();
        }
        Path startPath = Paths.get(dirPath);
        return listFilesRecursive(startPath, recursive)
                .parallel() // Run operations in parallel
                .runOn(Schedulers.boundedElastic()) // Use a scheduler suitable for I/O blocking work
                .sequential(); // Merge results back into a sequential Flux
    }

    private Flux<Object> listFilesRecursive(Path dirPath, boolean recursive) {
        return Flux.using(() -> Files.list(dirPath),
                        Flux::fromStream,
                        Stream::close)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(path -> {
                    if (Files.isDirectory(path) && recursive) {
                        // Recursively list files in subdirectories
                        return listFilesRecursive(path, true);
                    } else {
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                            return Flux.just(createFileInfo(path, attrs));
                        } catch (IOException e) {
                            log.atError().log("Error reading file attributes: {}", e.getMessage());
                            return Flux.error(e);
                        }
                    }
                });
    }

    private FileInfo createFileInfo(Path path, BasicFileAttributes attrs) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(path.toString());
        fileInfo.setSize(attrs.size());
        fileInfo.setCreationTime(attrs.creationTime().toMillis());
        fileInfo.setLastModifiedTime(attrs.lastModifiedTime().toMillis());
        return fileInfo;
    }

    @SneakyThrows
    private boolean isValidPath(String path) {
        if (path == null || path.trim().isBlank()) {
            log.warn("Path is null or blank.");
            return false;
        } else {
            Path resolvedPath = Path.of(path.trim());
            if (!Files.exists(resolvedPath)) {
                log.warn("Path '{}' does not exist.", path);
                throw new ResourceNotFoundException("Path does not exist.");
            } else if (!Files.isDirectory(resolvedPath)) {
                log.warn("Path '{}' is not a directory.", path);
                throw new ResourceNotFoundException("Path is not a directory.");
            } else if (!Files.isReadable(resolvedPath)) {
                log.warn("Path '{}' is not readable.", path);
                throw new ResourceAccessDeniedException("Path is not readable.");
            } else {
                return true;
            }
        }
    }
}