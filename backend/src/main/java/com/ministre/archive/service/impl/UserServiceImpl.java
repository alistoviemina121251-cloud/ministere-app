package com.ministre.archive.service.impl;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ministre.archive.model.User;
import com.ministre.archive.repository.UserRepository;
import com.ministre.archive.service.MailService;
import com.ministre.archive.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.mailService =
                mailService;
    }

    @Override
    public User inscrire(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                "Données utilisateur invalides."
            );
        }

        if (userRepository.existsByEmail(
                user.getEmail())) {

            throw new RuntimeException(
                "Cet email est déjà utilisé."
            );
        }

        if (userRepository.existsByNomUtilisateur(
                user.getNomUtilisateur())) {

            throw new RuntimeException(
                "Ce nom d'utilisateur est déjà utilisé."
            );
        }

        user.setPassword(
            passwordEncoder.encode(
                user.getPassword()
            )
        );

        user.setActif(false);

        user.setDateInscription(
            LocalDateTime.now()
        );

        user.setRole("USER");

        String code =
            String.format(
                "%06d",
                new Random().nextInt(1_000_000)
            );

        user.setCodeConfirmation(code);

        user.setCodeExpiration(
            LocalDateTime.now()
                .plusMinutes(15)
        );

        User savedUser =
                userRepository.save(user);

        mailService.envoyerCodeConfirmation(
            user.getEmail(),
            code
        );

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {

        if (email == null
                || email.isBlank()) {

            throw new IllegalArgumentException(
                "Email obligatoire."
            );
        }

        return userRepository
            .findByEmail(email)
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

            throw new RuntimeException(
                "Ce compte est déjà activé."
            );
        }

        if (code == null
                || !code.equals(
                    user.getCodeConfirmation())) {

            throw new RuntimeException(
                "Code de confirmation incorrect."
            );
        }

        if (user.getCodeExpiration() == null
                || user.getCodeExpiration()
                    .isBefore(
                        LocalDateTime.now()
                    )) {

            throw new RuntimeException(
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

            throw new RuntimeException(
                "Ce compte est déjà activé."
            );
        }

        String newCode =
            String.format(
                "%06d",
                new Random().nextInt(1_000_000)
            );

        user.setCodeConfirmation(
            newCode
        );

        user.setCodeExpiration(
            LocalDateTime.now()
                .plusMinutes(15)
        );

        userRepository.save(user);

        mailService.envoyerCodeConfirmation(
            user.getEmail(),
            newCode
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findByNomUtilisateur(
            String nomUtilisateur) {

        return userRepository
            .findByNomUtilisateur(
                nomUtilisateur
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

        return userRepository
            .existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNomUtilisateur(
            String nomUtilisateur) {

        return userRepository
            .existsByNomUtilisateur(
                nomUtilisateur
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

        mailService.envoyerTokenReinitialisation(
            user.getEmail(),
            token
        );
    }

    @Override
    public void resetPassword(
            String email,
            String token,
            String newPassword) {

        User user =
                findByEmail(email);

        if (user.getResetToken() == null
                || !user.getResetToken()
                    .equals(token)) {

            throw new RuntimeException(
                "Token invalide."
            );
        }

        if (user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry()
                    .isBefore(
                        LocalDateTime.now()
                    )) {

            throw new RuntimeException(
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
}