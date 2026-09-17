package com.ministre.archive.service.impl;

import com.ministre.archive.model.User;
import com.ministre.archive.repository.UserRepository;
import com.ministre.archive.service.EmailService;
import com.ministre.archive.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.emailService =
                emailService;
    }

    @Override
    public User inscrire(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Données utilisateur invalides."
            );
        }

        if (user.getNom() == null
                || user.getNom().isBlank()) {

            throw new IllegalArgumentException(
                    "Le nom est obligatoire."
            );
        }

        if (user.getPrenom() == null
                || user.getPrenom().isBlank()) {

            throw new IllegalArgumentException(
                    "Le prénom est obligatoire."
            );
        }

        if (user.getNomUtilisateur() == null
                || user.getNomUtilisateur().isBlank()) {

            throw new IllegalArgumentException(
                    "Le nom d'utilisateur est obligatoire."
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "L'email est obligatoire."
            );
        }

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Le mot de passe est obligatoire."
            );
        }

        String email =
                user.getEmail()
                        .trim()
                        .toLowerCase();

        String username =
                user.getNomUtilisateur()
                        .trim();

        if (userRepository
                .existsByEmail(email)) {

            throw new IllegalArgumentException(
                    "Cet email est déjà utilisé."
            );
        }

        if (userRepository
                .existsByNomUtilisateur(username)) {

            throw new IllegalArgumentException(
                    "Ce nom d'utilisateur est déjà utilisé."
            );
        }

        user.setEmail(email);

        user.setNomUtilisateur(username);

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        user.setActif(false);

        user.setDateInscription(
                LocalDateTime.now()
        );

        user.setDerniereConnexion(null);

        user.setRole("USER");

        String code =
                generateConfirmationCode();

        user.setCodeConfirmation(code);

        user.setCodeExpiration(
                LocalDateTime.now()
                        .plusMinutes(15)
        );

        user.setResetToken(null);

        user.setResetTokenExpiry(null);

        User savedUser =
                userRepository.save(user);

        /*
         * Envoi réel du code par email.
         */
        emailService.sendConfirmationCode(
                savedUser.getEmail(),
                code
        );

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(
            String email) {

        if (email == null
                || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email obligatoire."
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Utilisateur non trouvé."
                        )
                );
    }

    @Override
    public User activerCompte(
            String email,
            String code) {

        User user =
                findByEmail(email);

        if (Boolean.TRUE.equals(
                user.getActif())) {

            throw new IllegalStateException(
                    "Ce compte est déjà activé."
            );
        }

        if (code == null
                || code.isBlank()) {

            throw new IllegalArgumentException(
                    "Code de confirmation obligatoire."
            );
        }

        if (user.getCodeConfirmation() == null
                || !code.trim().equals(
                        user.getCodeConfirmation()
                )) {

            throw new IllegalArgumentException(
                    "Code de confirmation incorrect."
            );
        }

        if (user.getCodeExpiration() == null
                || user.getCodeExpiration()
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new IllegalArgumentException(
                    "Code de confirmation expiré."
            );
        }

        user.setActif(true);

        user.setCodeConfirmation(null);

        user.setCodeExpiration(null);

        return userRepository.save(user);
    }

    @Override
    public void generateNewCode(
            String email) {

        User user =
                findByEmail(email);

        if (Boolean.TRUE.equals(
                user.getActif())) {

            throw new IllegalStateException(
                    "Ce compte est déjà activé."
            );
        }

        String newCode =
                generateConfirmationCode();

        user.setCodeConfirmation(
                newCode
        );

        user.setCodeExpiration(
                LocalDateTime.now()
                        .plusMinutes(15)
        );

        userRepository.save(user);

        emailService.sendConfirmationCode(
                user.getEmail(),
                newCode
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findByNomUtilisateur(
            String nomUtilisateur) {

        if (nomUtilisateur == null
                || nomUtilisateur.isBlank()) {

            throw new IllegalArgumentException(
                    "Nom d'utilisateur obligatoire."
            );
        }

        return userRepository
                .findByNomUtilisateur(
                        nomUtilisateur.trim()
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Utilisateur non trouvé."
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(
            String email) {

        if (email == null
                || email.isBlank()) {

            return false;
        }

        return userRepository
                .existsByEmail(
                        email.trim().toLowerCase()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNomUtilisateur(
            String nomUtilisateur) {

        if (nomUtilisateur == null
                || nomUtilisateur.isBlank()) {

            return false;
        }

        return userRepository
                .existsByNomUtilisateur(
                        nomUtilisateur.trim()
                );
    }

    @Override
    public void updateLastLogin(
            String email) {

        User user =
                findByEmail(email);

        user.setDerniereConnexion(
                LocalDateTime.now()
        );

        userRepository.save(user);
    }

    @Override
    public void generateResetToken(
            String email) {

        User user =
                findByEmail(email);

        String token =
                UUID.randomUUID()
                        .toString();

        user.setResetToken(token);

        user.setResetTokenExpiry(
                LocalDateTime.now()
                        .plusMinutes(30)
        );

        userRepository.save(user);

        /*
         * Envoi du lien de réinitialisation
         * par email.
         */
        emailService.sendPasswordReset(
                user.getEmail(),
                token
        );
    }

    @Override
    public void resetPassword(
            String email,
            String token,
            String newPassword) {

        if (token == null
                || token.isBlank()) {

            throw new IllegalArgumentException(
                    "Token obligatoire."
            );
        }

        if (newPassword == null
                || newPassword.length() < 6) {

            throw new IllegalArgumentException(
                    "Le nouveau mot de passe doit "
                            + "contenir au moins 6 caractères."
            );
        }

        User user =
                findByEmail(email);

        if (user.getResetToken() == null
                || !user.getResetToken()
                .equals(token)) {

            throw new IllegalArgumentException(
                    "Token invalide."
            );
        }

        if (user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry()
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new IllegalArgumentException(
                    "Token expiré."
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        user.setResetToken(null);

        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    private String generateConfirmationCode() {

        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );
    }
}