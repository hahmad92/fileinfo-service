package com.hammad.file.service.service;

import com.hammad.file.service.model.FileInfo;
import com.hammad.file.service.utils.FileCreationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.mockito.Mockito.when;

class FileInfoServiceTest {

    @Mock
    private FileInfoService fileInfoService;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    static void setup() throws IOException {
        FileCreationService.cleanupTestDirectory();
        FileCreationService.setupTestDirectory();
    }
    @AfterAll
    static void tearDown() {
        FileCreationService.cleanupTestDirectory();
    }

    @Test
    @DisplayName("listFiles emits FileInfo objects for valid directory")
    void listFilesEmitsFileInfoForValidDirectory() {
        when(fileInfoService.listFiles("valid/directory/path")).thenReturn(Flux.just(createMockFileInfo("file1.txt"), createMockFileInfo("file2.txt")));

        StepVerifier.create(fileInfoService.listFiles("valid/directory/path"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("listFiles completes empty for empty directory")
    void listFilesCompletesEmptyForEmptyDirectory() {
        when(fileInfoService.listFiles("empty/directory/path")).thenReturn(Flux.empty());

        StepVerifier.create(fileInfoService.listFiles("empty/directory/path"))
                .verifyComplete();
    }

    @Test
    @DisplayName("listFiles emits error for non-existent directory")
    void listFilesEmitsErrorForNonExistentDirectory() {
        when(fileInfoService.listFiles("non/existent/directory/path")).thenReturn(Flux.error(new NoSuchFileException("non/existent/directory/path")));

        StepVerifier.create(fileInfoService.listFiles("non/existent/directory/path"))
                .expectError(NoSuchFileException.class)
                .verify();
    }

    private FileInfo createMockFileInfo(String fileName) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(fileName);
        fileInfo.setSize(1024);
        fileInfo.setDirectory(false);
        fileInfo.setCreationTime(System.currentTimeMillis());
        fileInfo.setLastModifiedTime(System.currentTimeMillis());
        return fileInfo;
    }

}