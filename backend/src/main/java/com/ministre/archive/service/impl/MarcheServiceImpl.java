package com.ministre.archive.service.impl;

import com.ministre.archive.model.Marche;
import com.ministre.archive.model.PieceJointe;
import com.ministre.archive.repository.MarcheRepository;
import com.ministre.archive.repository.PieceJointeRepository;
import com.ministre.archive.service.FileStorageService;
import com.ministre.archive.service.MarcheService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MarcheServiceImpl implements MarcheService {

    private final MarcheRepository marcheRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final FileStorageService fileStorageService;

    public MarcheServiceImpl(
            MarcheRepository marcheRepository,
            PieceJointeRepository pieceJointeRepository,
            FileStorageService fileStorageService) {

        this.marcheRepository = marcheRepository;
        this.pieceJointeRepository = pieceJointeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marche> getAllMarches() {

        return marcheRepository
                .findAllOrderByDateCreationDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Marche getMarcheById(Long id) {

        return marcheRepository
                .findById(id)
                .orElseThrow(() ->
                    new NoSuchElementException(
                        "Marché non trouvé."
                    )
                );
    }

    @Override
    @Transactional
    public Marche createMarche(Marche marche) {

        if (marche.getStatut() == null) {
            marche.setStatut(
                com.ministre.archive.model.StatutArchive.EN_ATTENTE
            );
        }

        return marcheRepository.save(marche);
    }

    @Override
    @Transactional
    public Marche updateMarche(
            Long id,
            Marche marcheDetails) {

        Marche existing =
                getMarcheById(id);

        existing.setReference(
            marcheDetails.getReference()
        );

        existing.setObjet(
            marcheDetails.getObjet()
        );

        existing.setDescription(
            marcheDetails.getDescription()
        );

        existing.setMontant(
            marcheDetails.getMontant()
        );

        existing.setDuree(
            marcheDetails.getDuree()
        );

        existing.setRegion(
            marcheDetails.getRegion()
        );

        existing.setDepartement(
            marcheDetails.getDepartement()
        );

        existing.setCommune(
            marcheDetails.getCommune()
        );

        existing.setLieuExecution(
            marcheDetails.getLieuExecution()
        );

        existing.setDirection(
            marcheDetails.getDirection()
        );

        existing.setType(
            marcheDetails.getType()
        );

        if (marcheDetails.getStatut() != null) {
            existing.setStatut(
                marcheDetails.getStatut()
            );
        }

        return marcheRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteMarche(Long id) {

        Marche marche =
                getMarcheById(id);

        List<PieceJointe> pieces =
                pieceJointeRepository
                    .findByMarcheId(id);

        for (PieceJointe piece : pieces) {

            try {

                if (piece.getChemin() != null) {

                    fileStorageService.delete(
                        Paths.get(piece.getChemin())
                    );
                }

            } catch (IOException e) {

                System.err.println(
                    "Impossible de supprimer le fichier : "
                    + e.getMessage()
                );
            }
        }

        pieceJointeRepository.deleteAll(pieces);

        marcheRepository.delete(marche);
    }

    // =====================================================
    // PIÈCES JOINTES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<PieceJointe> getPiecesByMarche(
            Long marcheId) {

        getMarcheById(marcheId);

        return pieceJointeRepository
                .findByMarcheId(marcheId);
    }

    @Override
    @Transactional
    public PieceJointe uploadPieceJointe(
            Long marcheId,
            MultipartFile file) {

        Marche marche =
                getMarcheById(marcheId);

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                "Veuillez sélectionner un fichier."
            );
        }

        try {

            Path savedPath =
                    fileStorageService.save(
                        marcheId,
                        file
                    );

            PieceJointe piece =
                    new PieceJointe();

            piece.setNom(
                file.getOriginalFilename()
            );

            piece.setType(
                getFileExtension(
                    file.getOriginalFilename()
                )
            );

            piece.setTaille(
                file.getSize()
            );

            piece.setChemin(
                savedPath.toString()
            );

            piece.setMarche(
                marche
            );

            return pieceJointeRepository.save(piece);

        } catch (IOException e) {

            throw new RuntimeException(
                "Erreur pendant l'upload du fichier.",
                e
            );
        }
    }

    @Override
    @Transactional
    public void deletePieceJointe(
            Long marcheId,
            Long pieceId) {

        getMarcheById(marcheId);

        PieceJointe piece =
                pieceJointeRepository
                    .findById(pieceId)
                    .orElseThrow(() ->
                        new NoSuchElementException(
                            "Pièce jointe non trouvée."
                        )
                    );

        if (piece.getMarche() == null
                || !marcheId.equals(
                    piece.getMarche().getId())) {

            throw new IllegalArgumentException(
                "Cette pièce jointe n'appartient pas à ce marché."
            );
        }

        try {

            if (piece.getChemin() != null) {

                fileStorageService.delete(
                    Paths.get(piece.getChemin())
                );
            }

        } catch (IOException e) {

            System.err.println(
                "Erreur suppression fichier : "
                + e.getMessage()
            );
        }

        pieceJointeRepository.delete(piece);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadPieceJointe(
            Long marcheId,
            Long pieceId) {

        getMarcheById(marcheId);

        PieceJointe piece =
                pieceJointeRepository
                    .findById(pieceId)
                    .orElseThrow(() ->
                        new NoSuchElementException(
                            "Pièce jointe non trouvée."
                        )
                    );

        if (piece.getMarche() == null
                || !marcheId.equals(
                    piece.getMarche().getId())) {

            throw new IllegalArgumentException(
                "Cette pièce jointe n'appartient pas à ce marché."
            );
        }

        if (piece.getChemin() == null) {

            throw new RuntimeException(
                "Chemin du fichier manquant."
            );
        }

        try {

            return fileStorageService.read(
                Paths.get(piece.getChemin())
            );

        } catch (IOException e) {

            throw new RuntimeException(
                "Erreur lors du téléchargement.",
                e
            );
        }
    }

    private String getFileExtension(
            String filename) {

        if (filename == null) {
            return "unknown";
        }

        int lastDot =
                filename.lastIndexOf('.');

        if (lastDot > 0
                && lastDot < filename.length() - 1) {

            return filename
                    .substring(lastDot + 1)
                    .toLowerCase();
        }

        return "unknown";
    }
}