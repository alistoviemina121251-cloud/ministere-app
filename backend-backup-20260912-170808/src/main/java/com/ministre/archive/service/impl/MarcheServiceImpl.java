package com.ministre.archive.service.impl;

import com.ministre.archive.model.Marche;
import com.ministre.archive.model.PieceJointe;
import com.ministre.archive.model.StatutArchive;
import com.ministre.archive.repository.MarcheRepository;
import com.ministre.archive.repository.PieceJointeRepository;
import com.ministre.archive.service.FileStorageService;
import com.ministre.archive.service.MarcheService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class MarcheServiceImpl
        implements MarcheService {

    private final MarcheRepository marcheRepository;

    private final PieceJointeRepository
            pieceJointeRepository;

    private final FileStorageService
            fileStorageService;

    public MarcheServiceImpl(
            MarcheRepository marcheRepository,
            PieceJointeRepository pieceJointeRepository,
            FileStorageService fileStorageService) {

        this.marcheRepository =
                marcheRepository;

        this.pieceJointeRepository =
                pieceJointeRepository;

        this.fileStorageService =
                fileStorageService;
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

        if (id == null) {
            throw new IllegalArgumentException(
                    "Identifiant du marché obligatoire."
            );
        }

        return marcheRepository
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Marché non trouvé."
                        )
                );
    }

    @Override
    public Marche createMarche(
            Marche marche) {

        if (marche == null) {
            throw new IllegalArgumentException(
                    "Les données du marché sont obligatoires."
            );
        }

        if (marche.getReference() == null
                || marche.getReference().isBlank()) {

            throw new IllegalArgumentException(
                    "La référence est obligatoire."
            );
        }

        if (marche.getObjet() == null
                || marche.getObjet().isBlank()) {

            throw new IllegalArgumentException(
                    "L'objet du marché est obligatoire."
            );
        }

        if (marche.getMontant() != null
                && marche.getMontant() < 0) {

            throw new IllegalArgumentException(
                    "Le montant ne peut pas être négatif."
            );
        }

        if (marcheRepository
                .findByReference(
                        marche.getReference()
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Cette référence de marché existe déjà."
            );
        }

        if (marche.getStatut() == null) {

            marche.setStatut(
                    StatutArchive.EN_ATTENTE
            );
        }

        /*
         * On laisse JPA gérer les dates.
         */
        marche.setId(null);

        return marcheRepository.save(marche);
    }

    @Override
    public Marche updateMarche(
            Long id,
            Marche marcheDetails) {

        if (marcheDetails == null) {
            throw new IllegalArgumentException(
                    "Les données du marché sont obligatoires."
            );
        }

        Marche existing =
                getMarcheById(id);

        if (marcheDetails.getReference() == null
                || marcheDetails.getReference().isBlank()) {

            throw new IllegalArgumentException(
                    "La référence est obligatoire."
            );
        }

        if (marcheDetails.getObjet() == null
                || marcheDetails.getObjet().isBlank()) {

            throw new IllegalArgumentException(
                    "L'objet du marché est obligatoire."
            );
        }

        if (marcheDetails.getMontant() != null
                && marcheDetails.getMontant() < 0) {

            throw new IllegalArgumentException(
                    "Le montant ne peut pas être négatif."
            );
        }

        marcheRepository
                .findByReference(
                        marcheDetails.getReference()
                )
                .ifPresent(other -> {

                    if (!other.getId().equals(id)) {

                        throw new IllegalArgumentException(
                                "Cette référence de marché "
                                        + "est déjà utilisée."
                        );
                    }
                });

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
    public void deleteMarche(Long id) {

        Marche marche =
                getMarcheById(id);

        List<PieceJointe> pieces =
                pieceJointeRepository
                        .findByMarcheId(id);

        for (PieceJointe piece : pieces) {

            if (piece.getChemin() == null) {
                continue;
            }

            try {

                fileStorageService.delete(
                        Paths.get(
                                piece.getChemin()
                        )
                );

            } catch (IOException e) {

                throw new RuntimeException(
                        "Impossible de supprimer "
                                + "le fichier : "
                                + piece.getNom(),
                        e
                );
            }
        }

        pieceJointeRepository
                .deleteAll(pieces);

        marcheRepository.delete(marche);
    }

    // =====================================================
    // PIÈCES JOINTES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<PieceJointe>
    getPiecesByMarche(
            Long marcheId) {

        getMarcheById(marcheId);

        return pieceJointeRepository
                .findByMarcheId(marcheId);
    }

    @Override
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

        /*
         * Limite supplémentaire côté Java.
         */
        long maxSize =
                20L * 1024L * 1024L;

        if (file.getSize() > maxSize) {

            throw new IllegalArgumentException(
                    "Le fichier ne doit pas dépasser 20 Mo."
            );
        }

        try {

            var savedPath =
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

            return pieceJointeRepository
                    .save(piece);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erreur pendant l'upload du fichier.",
                    e
            );
        }
    }

    @Override
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
                        piece.getMarche().getId()
                )) {

            throw new IllegalArgumentException(
                    "Cette pièce jointe "
                            + "n'appartient pas à ce marché."
            );
        }

        try {

            if (piece.getChemin() != null) {

                fileStorageService.delete(
                        Paths.get(
                                piece.getChemin()
                        )
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erreur lors de la suppression "
                            + "du fichier.",
                    e
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
                        piece.getMarche().getId()
                )) {

            throw new IllegalArgumentException(
                    "Cette pièce jointe "
                            + "n'appartient pas à ce marché."
            );
        }

        if (piece.getChemin() == null
                || piece.getChemin().isBlank()) {

            throw new RuntimeException(
                    "Chemin du fichier manquant."
            );
        }

        try {

            return fileStorageService.read(
                    Paths.get(
                            piece.getChemin()
                    )
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

        if (filename == null
                || filename.isBlank()) {

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