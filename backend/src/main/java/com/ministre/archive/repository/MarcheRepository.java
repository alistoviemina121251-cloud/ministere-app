package com.ministre.archive.repository;

import com.ministre.archive.model.Marche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MarcheRepository extends JpaRepository<Marche, Long> {
    @Query("SELECT m FROM Marche m ORDER BY m.dateCreation DESC")
    List<Marche> findAllOrderByDateCreationDesc();
}
