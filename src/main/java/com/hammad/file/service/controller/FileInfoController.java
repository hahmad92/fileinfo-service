package com.hammad.file.service.controller;

import com.hammad.file.service.service.FileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController
@Log4j2
@RequiredArgsConstructor
public class FileInfoController {

    private final FileInfoService fileInfoService;

    @GetMapping("/list-files")

    public Mono<ResponseEntity<Flux<Object>>> listFiles(@RequestParam String path) {
        log.info("Request to list files in path: {}", path);
        Flux<Object> fileInfos = fileInfoService.listFiles(path);
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(fileInfos));
    }
}