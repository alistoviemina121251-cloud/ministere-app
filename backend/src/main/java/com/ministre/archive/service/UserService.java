package com.ministre.archive.service;

import com.ministre.archive.model.User;

public interface UserService {
    User inscrire(User user);
    User findByEmail(String email);
    User activerCompte(String email, String code);
    void generateNewCode(String email);
    User findByNomUtilisateur(String nomUtilisateur);
    boolean existsByEmail(String email);
    boolean existsByNomUtilisateur(String nomUtilisateur);
    void updateLastLogin(String email);
    void generateResetToken(String email);
    void resetPassword(String email, String token, String newPassword);
}
