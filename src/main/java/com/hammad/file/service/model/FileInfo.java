package com.hammad.file.service.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileInfo {
    // Getters and Setters
    private String path;
    @JsonProperty("sz")
    private long size;
    private boolean isDirectory;
    //Change json property name to ct
    @JsonProperty("ct")
    private long creationTime;
    @JsonProperty("lmt")
    private long lastModifiedTime;
}