package com.ministre.archive.repository;

import com.ministre.archive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNomUtilisateur(String nomUtilisateur);
    boolean existsByEmail(String email);
    boolean existsByNomUtilisateur(String nomUtilisateur);
}
