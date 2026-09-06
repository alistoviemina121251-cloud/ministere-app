package com.ministre.archive.service.impl;

import com.ministre.archive.model.Marche;
import com.ministre.archive.model.PieceJointe;
import com.ministre.archive.repository.MarcheRepository;
import com.ministre.archive.repository.PieceJointeRepository;
import com.ministre.archive.service.MarcheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MarcheServiceImpl implements MarcheService {
    private final MarcheRepository marcheRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final String uploadDir = "uploads/";

    public MarcheServiceImpl(MarcheRepository marcheRepository, PieceJointeRepository pieceJointeRepository) {
        this.marcheRepository = marcheRepository;
        this.pieceJointeRepository = pieceJointeRepository;
    }

    @Override @Transactional(readOnly = true)
    public List<Marche> getAllMarches() { return marcheRepository.findAllOrderByDateCreationDesc(); }

    @Override @Transactional(readOnly = true)
    public Marche getMarcheById(Long id) { return marcheRepository.findById(id).orElseThrow(() -> new NoSuchElementException("March? non trouv?")); }

    @Override @Transactional
    public Marche createMarche(Marche marche) { return marcheRepository.save(marche); }

    @Override @Transactional
    public Marche updateMarche(Long id, Marche marcheDetails) {
        Marche existing = getMarcheById(id);
        existing.setReference(marcheDetails.getReference());
        existing.setObjet(marcheDetails.getObjet());
        existing.setDescription(marcheDetails.getDescription());
        existing.setMontant(marcheDetails.getMontant());
        existing.setDuree(marcheDetails.getDuree());
        existing.setRegion(marcheDetails.getRegion());
        existing.setDepartement(marcheDetails.getDepartement());
        existing.setCommune(marcheDetails.getCommune());
        existing.setLieuExecution(marcheDetails.getLieuExecution());
        existing.setDirection(marcheDetails.getDirection());
        existing.setType(marcheDetails.getType());
        existing.setStatut(marcheDetails.getStatut());
        return marcheRepository.save(existing);
    }

    @Override @Transactional
    public void deleteMarche(Long id) { marcheRepository.deleteById(id); }

    // ===== PI?CES JOINTES =====
    @Override
    public List<PieceJointe> getPiecesByMarche(Long marcheId) {
        return pieceJointeRepository.findByMarcheId(marcheId);
    }

    @Override
    @Transactional
    public PieceJointe uploadPieceJointe(Long marcheId, MultipartFile file) {
        try {
            Marche marche = getMarcheById(marcheId);
            Path uploadPath = Paths.get(uploadDir + marcheId);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            PieceJointe piece = new PieceJointe();
            piece.setNom(file.getOriginalFilename());
            piece.setType(getFileExtension(file.getOriginalFilename()));
            piece.setTaille(file.getSize());
            piece.setChemin(filePath.toString());
            piece.setMarche(marche);
            return pieceJointeRepository.save(piece);
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deletePieceJointe(Long marcheId, Long pieceId) {
        pieceJointeRepository.deleteById(pieceId);
    }

    @Override
    public byte[] downloadPieceJointe(Long marcheId, Long pieceId) {
        try {
            PieceJointe piece = pieceJointeRepository.findById(pieceId)
                .orElseThrow(() -> new NoSuchElementException("Pi?ce jointe non trouv?e"));
            return Files.readAllBytes(Paths.get(piece.getChemin()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur t?l?chargement: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "default";
    }
}
