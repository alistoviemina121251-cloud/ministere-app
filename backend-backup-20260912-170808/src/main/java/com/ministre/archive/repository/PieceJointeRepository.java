package com.ministre.archive.repository;

import com.ministre.archive.model.PieceJointe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PieceJointeRepository extends JpaRepository<PieceJointe, Long> {
    List<PieceJointe> findByMarcheId(Long marcheId);
}
