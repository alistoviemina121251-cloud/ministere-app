package com.ministre.archive.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {

    Path save(
            Long marcheId,
            MultipartFile file
    ) throws IOException;

    byte[] read(
            Path path
    ) throws IOException;

    void delete(
            Path path
    ) throws IOException;
}