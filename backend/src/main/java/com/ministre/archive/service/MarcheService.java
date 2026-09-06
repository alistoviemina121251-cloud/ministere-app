package com.ministre.archive.service;

import com.ministre.archive.model.Marche;
import com.ministre.archive.model.PieceJointe;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MarcheService {
    List<Marche> getAllMarches();
    Marche getMarcheById(Long id);
    Marche createMarche(Marche marche);
    Marche updateMarche(Long id, Marche marche);
    void deleteMarche(Long id);
    
    // Pi?ces jointes
    List<PieceJointe> getPiecesByMarche(Long marcheId);
    PieceJointe uploadPieceJointe(Long marcheId, MultipartFile file);
    void deletePieceJointe(Long marcheId, Long pieceId);
    byte[] downloadPieceJointe(Long marcheId, Long pieceId);
}
