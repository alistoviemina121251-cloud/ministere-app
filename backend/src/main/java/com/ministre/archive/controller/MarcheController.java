package com.ministre.archive.controller;

import com.ministre.archive.model.Marche;
import com.ministre.archive.model.PieceJointe;
import com.ministre.archive.service.MarcheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/marches")
@CrossOrigin(origins = "http://localhost:4200")
public class MarcheController {
    private final MarcheService marcheService;
    public MarcheController(MarcheService marcheService) { this.marcheService = marcheService; }

    @GetMapping public ResponseEntity<List<Marche>> getAllMarches() { return ResponseEntity.ok(marcheService.getAllMarches()); }
    @GetMapping("/{id}") public ResponseEntity<Marche> getMarcheById(@PathVariable Long id) { return ResponseEntity.ok(marcheService.getMarcheById(id)); }
    @PostMapping public ResponseEntity<Marche> createMarche(@Valid @RequestBody Marche marche) { return new ResponseEntity<>(marcheService.createMarche(marche), HttpStatus.CREATED); }
    @PutMapping("/{id}") public ResponseEntity<Marche> updateMarche(@PathVariable Long id, @Valid @RequestBody Marche marche) { return ResponseEntity.ok(marcheService.updateMarche(id, marche)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deleteMarche(@PathVariable Long id) { marcheService.deleteMarche(id); return ResponseEntity.noContent().build(); }

    // ===== PI?CES JOINTES =====
    @GetMapping("/{marcheId}/pieces")
    public ResponseEntity<List<PieceJointe>> getPiecesByMarche(@PathVariable Long marcheId) {
        return ResponseEntity.ok(marcheService.getPiecesByMarche(marcheId));
    }

    @PostMapping("/{marcheId}/pieces")
    public ResponseEntity<PieceJointe> uploadPieceJointe(@PathVariable Long marcheId, @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(marcheService.uploadPieceJointe(marcheId, file), HttpStatus.CREATED);
    }

    @DeleteMapping("/{marcheId}/pieces/{pieceId}")
    public ResponseEntity<Void> deletePieceJointe(@PathVariable Long marcheId, @PathVariable Long pieceId) {
        marcheService.deletePieceJointe(marcheId, pieceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{marcheId}/pieces/{pieceId}")
    public ResponseEntity<byte[]> downloadPieceJointe(@PathVariable Long marcheId, @PathVariable Long pieceId) {
        return ResponseEntity.ok(marcheService.downloadPieceJointe(marcheId, pieceId));
    }
}
