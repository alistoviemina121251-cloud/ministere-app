package com.ministre.archive.service.impl;

import com.ministre.archive.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadRoot;

    public FileStorageServiceImpl(
            @Value("${app.upload.dir:/var/data/uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Impossible de créer le dossier des uploads : " + uploadRoot, e);
        }
    }

    @Override
    public Path save(Long marcheId, MultipartFile file) throws IOException {
        if (marcheId == null) {
            throw new IllegalArgumentException("L'identifiant du marché est obligatoire.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("Nom de fichier invalide.");
        }

        String safeName = Paths.get(originalName).getFileName().toString();
        String uniqueName = UUID.randomUUID() + "_" + safeName;

        Path marcheDirectory = uploadRoot.resolve(String.valueOf(marcheId)).normalize();
        if (!marcheDirectory.startsWith(uploadRoot)) {
            throw new SecurityException("Chemin de fichier invalide.");
        }

        Files.createDirectories(marcheDirectory);

        Path target = marcheDirectory.resolve(uniqueName).normalize();
        if (!target.startsWith(marcheDirectory)) {
            throw new SecurityException("Chemin de fichier invalide.");
        }

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    @Override
    public byte[] read(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Le chemin du fichier est obligatoire.");
        }

        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(uploadRoot)) {
            throw new SecurityException("Accès au fichier refusé.");
        }
        if (!Files.exists(normalized) || !Files.isRegularFile(normalized)) {
            throw new IOException("Fichier introuvable.");
        }
        return Files.readAllBytes(normalized);
    }

    @Override
    public void delete(Path path) throws IOException {
        if (path == null) {
            return;
        }

        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(uploadRoot)) {
            throw new SecurityException("Suppression du fichier refusée.");
        }
        Files.deleteIfExists(normalized);
    }
}
