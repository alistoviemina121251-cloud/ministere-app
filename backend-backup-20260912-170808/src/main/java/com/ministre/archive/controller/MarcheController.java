package com.ministre.archive.controller;

import com.ministre.archive.model.Marche;
import com.ministre.archive.model.PieceJointe;
import com.ministre.archive.service.MarcheService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/marches")
@CrossOrigin(
        origins = "${FRONTEND_URL:http://localhost:4200}",
        allowCredentials = "true"
)
public class MarcheController {

    private final MarcheService marcheService;

    public MarcheController(
            MarcheService marcheService) {

        this.marcheService =
                marcheService;
    }

    // =====================================================
    // MARCHÉS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Marche>>
    getAllMarches() {

        return ResponseEntity.ok(
                marcheService.getAllMarches()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marche>
    getMarcheById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                marcheService.getMarcheById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Marche>
    createMarche(
            @Valid @RequestBody Marche marche) {

        Marche saved =
                marcheService.createMarche(
                        marche
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marche>
    updateMarche(
            @PathVariable Long id,
            @Valid @RequestBody Marche marche) {

        return ResponseEntity.ok(
                marcheService.updateMarche(
                        id,
                        marche
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteMarche(
            @PathVariable Long id) {

        marcheService.deleteMarche(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // =====================================================
    // PIÈCES JOINTES
    // =====================================================

    @GetMapping("/{marcheId}/pieces")
    public ResponseEntity<List<PieceJointe>>
    getPiecesByMarche(
            @PathVariable Long marcheId) {

        return ResponseEntity.ok(
                marcheService.getPiecesByMarche(
                        marcheId
                )
        );
    }

    @PostMapping(
            value = "/{marcheId}/pieces",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<PieceJointe>
    uploadPieceJointe(
            @PathVariable Long marcheId,
            @RequestParam("file")
            MultipartFile file) {

        PieceJointe piece =
                marcheService.uploadPieceJointe(
                        marcheId,
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(piece);
    }

    @DeleteMapping(
            "/{marcheId}/pieces/{pieceId}"
    )
    public ResponseEntity<Void>
    deletePieceJointe(
            @PathVariable Long marcheId,
            @PathVariable Long pieceId) {

        marcheService.deletePieceJointe(
                marcheId,
                pieceId
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(
            "/{marcheId}/pieces/{pieceId}"
    )
    public ResponseEntity<byte[]>
    downloadPieceJointe(
            @PathVariable Long marcheId,
            @PathVariable Long pieceId) {

        byte[] file =
                marcheService.downloadPieceJointe(
                        marcheId,
                        pieceId
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(file);
    }
}