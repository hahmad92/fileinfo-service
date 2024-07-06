package com.hammad.file.service.controller;

import com.hammad.file.service.exception.ResourceNotFoundException;
import com.hammad.file.service.service.FileInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
;

class FileInfoControllerTest {

    @Mock
    private FileInfoService fileInfoService;

    @InjectMocks
    private FileInfoController fileInfoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return list of files for valid path")
    void shouldReturnListOfFilesForValidPath() {
        // Given
        String path = "/valid/path";
        Flux<Object> expectedFiles = Flux.just("file1.txt", "file2.txt");
        when(fileInfoService.listFiles(eq(path))).thenReturn(expectedFiles);

        ResponseEntity<Flux<Object>> response = fileInfoController.listFiles(path);

        assert response.getStatusCode() == HttpStatus.OK;
        StepVerifier.create(response.getBody())
                .expectNext("file1.txt", "file2.txt")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty list for path with no files")
    void shouldReturnEmptyListForPathWithNoFiles() {
        String path = "/empty/path";
        Flux<Object> expectedFiles = Flux.empty();
        when(fileInfoService.listFiles(eq(path))).thenReturn(expectedFiles);

        ResponseEntity<Flux<Object>> response = fileInfoController.listFiles(path);

        assert response.getStatusCode() == HttpStatus.OK;
        StepVerifier.create(response.getBody())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return HttpStatus.OK for valid path with no files")
    void shouldReturnHttpStatusOKForValidPathWithNoFiles() {
        String pathWithNoFiles = "/valid/empty/path";
        when(fileInfoService.listFiles(eq(pathWithNoFiles))).thenReturn(Flux.empty());

        ResponseEntity<Flux<Object>> response = fileInfoController.listFiles(pathWithNoFiles);

        assert response.getStatusCode() == HttpStatus.OK;
        StepVerifier.create(response.getBody())
                .verifyComplete();
    }
}