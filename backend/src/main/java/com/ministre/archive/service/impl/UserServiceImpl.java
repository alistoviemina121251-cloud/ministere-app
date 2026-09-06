package com.ministre.archive.service.impl;

import com.ministre.archive.model.User;
import com.ministre.archive.repository.UserRepository;
import com.ministre.archive.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User inscrire(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Cet email est d?j? utilis?");
        }
        if (userRepository.existsByNomUtilisateur(user.getNomUtilisateur())) {
            throw new RuntimeException("Ce nom d'utilisateur est d?j? utilis?");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActif(false);
        user.setDateInscription(LocalDateTime.now());
        user.setRole("USER");
        
        String code = String.format("%06d", new Random().nextInt(1000000));
        user.setCodeConfirmation(code);
        user.setCodeExpiration(LocalDateTime.now().plusMinutes(15));
        
        User savedUser = userRepository.save(user);
        
        System.out.println("=========================================");
        System.out.println("?? INSCRIPTION - CODE DE CONFIRMATION");
        System.out.println("?? Email: " + user.getEmail());
        System.out.println("?? Code: " + code);
        System.out.println("? Expiration: 15 minutes");
        System.out.println("=========================================");
        
        return savedUser;
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouv?"));
    }
    
    @Override
    public User activerCompte(String email, String code) {
        User user = findByEmail(email);
        
        if (user.getActif()) {
            throw new RuntimeException("Ce compte est d?j? activ?");
        }
        
        if (!code.equals(user.getCodeConfirmation())) {
            throw new RuntimeException("Code de confirmation incorrect");
        }
        
        if (user.getCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Code de confirmation expir? (15 minutes)");
        }
        
        user.setActif(true);
        user.setCodeConfirmation(null);
        user.setCodeExpiration(null);
        
        System.out.println("? Compte activ? pour: " + email);
        return userRepository.save(user);
    }
    
    @Override
    public void generateNewCode(String email) {
        User user = findByEmail(email);
        
        if (user.getActif()) {
            throw new RuntimeException("Ce compte est d?j? activ?");
        }
        
        String newCode = String.format("%06d", new Random().nextInt(1000000));
        user.setCodeConfirmation(newCode);
        user.setCodeExpiration(LocalDateTime.now().plusMinutes(15));
        
        userRepository.save(user);
        
        System.out.println("=========================================");
        System.out.println("?? NOUVEAU CODE DE CONFIRMATION");
        System.out.println("?? Email: " + user.getEmail());
        System.out.println("?? Nouveau code: " + newCode);
        System.out.println("? Expiration: 15 minutes");
        System.out.println("=========================================");
    }
    
    @Override
    public User findByNomUtilisateur(String nomUtilisateur) {
        return userRepository.findByNomUtilisateur(nomUtilisateur)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouv?"));
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByNomUtilisateur(String nomUtilisateur) {
        return userRepository.existsByNomUtilisateur(nomUtilisateur);
    }
    
    @Override
    public void updateLastLogin(String email) {
        User user = findByEmail(email);
        user.setDerniereConnexion(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void generateResetToken(String email) {
        User user = findByEmail(email);
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        
        System.out.println("=========================================");
        System.out.println("?? R?INITIALISATION DU MOT DE PASSE");
        System.out.println("?? Email: " + email);
        System.out.println("?? Token: " + token);
        System.out.println("? Expiration: 30 minutes");
        System.out.println("=========================================");
    }
    
    @Override
    public void resetPassword(String email, String token, String newPassword) {
        User user = findByEmail(email);
        
        if (user.getResetToken() == null || !user.getResetToken().equals(token)) {
            throw new RuntimeException("Token invalide");
        }
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expir?");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        
        System.out.println("? Mot de passe r?initialis? pour: " + email);
    }
}
