package com.ministre.archive.controller;

import com.ministre.archive.model.User;
import com.ministre.archive.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/inscrire")
    public ResponseEntity<Map<String, Object>> inscrire(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User savedUser = userService.inscrire(user);
            response.put("success", true);
            response.put("message", "Un code de confirmation vous a été envoyé par email.");
            response.put("email", savedUser.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/confirmer")
    public ResponseEntity<Map<String, Object>> confirmer(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.activerCompte(payload.get("email"), payload.get("code"));
            response.put("success", true);
            response.put("message", "Compte activé avec succès !");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/renvoyer-code")
    public ResponseEntity<Map<String, Object>> renvoyerCode(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.generateNewCode(payload.get("email"));
            response.put("success", true);
            response.put("message", "Un nouveau code vous a été envoyé par email.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> credentials,
            HttpServletRequest request,
            HttpServletResponse httpResponse) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.findByEmail(credentials.get("email"));

            if (!user.getActif()) {
                response.put("success", false);
                response.put("message", "Compte non activé. Vérifiez votre email.");
                return ResponseEntity.status(401).body(response);
            }

            if (!passwordEncoder.matches(credentials.get("password"), user.getPassword())) {
                response.put("success", false);
                response.put("message", "Email ou mot de passe incorrect");
                return ResponseEntity.status(401).body(response);
            }

            // Étape essentielle qui manquait : établir la session Spring Security
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, httpResponse);

            userService.updateLastLogin(user.getEmail());

            response.put("success", true);
            response.put("message", "Connexion réussie");
            response.put("email", user.getEmail());
            response.put("nomUtilisateur", user.getNomUtilisateur());
            response.put("nom", user.getNom() + " " + user.getPrenom());
            response.put("role", user.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.generateResetToken(payload.get("email"));
            response.put("success", true);
            response.put("message", "Un lien de réinitialisation vous a été envoyé par email.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.resetPassword(payload.get("email"), payload.get("token"), payload.get("newPassword"));
            response.put("success", true);
            response.put("message", "Mot de passe réinitialisé avec succès !");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }

        User user = userService.findByEmail(auth.getName());
        response.put("success", true);
        response.put("email", user.getEmail());
        response.put("nom", user.getNom() + " " + user.getPrenom());
        response.put("role", user.getRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Déconnexion réussie");
        return ResponseEntity.ok(response);
    }
}