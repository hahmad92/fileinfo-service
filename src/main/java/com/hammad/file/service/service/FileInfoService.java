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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Service
@Log4j2
public class FileInfoService {
    public Flux<Object> listFiles(String dirPath) {
        if (!isValidPath(dirPath)) {
            return Flux.empty();
        }
        return Flux.create(fluxSink -> {
            try {
                Path startPath = Paths.get(dirPath);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(startPath)) {
                    for (Path path : stream) {
                        if (!fluxSink.isCancelled()) {
                            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                            FileInfo fileInfo = createFileInfo(path, attrs);
                            fluxSink.next(fileInfo);
                        } else {
                            break;
                        }
                    }
                }
                fluxSink.complete();
            } catch (NoSuchFileException exception) {
                log.atError().log("File not found: {}", exception.getMessage());
                fluxSink.error(exception);
            } catch (IOException ex) {
                log.atError().log("Error listing files: {}", ex);
                fluxSink.error(ex);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private FileInfo createFileInfo(Path path, BasicFileAttributes attrs) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(path.toString());
        fileInfo.setSize(attrs.size());
        fileInfo.setDirectory(attrs.isDirectory());
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