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
@CrossOrigin(
        origins = "${FRONTEND_URL:http://localhost:4200}",
        allowCredentials = "true"
)
public class AuthController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextRepository
            securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder) {

        this.userService =
                userService;

        this.passwordEncoder =
                passwordEncoder;
    }

    // =====================================================
    // INSCRIPTION
    // =====================================================

    @PostMapping("/inscrire")
    public ResponseEntity<Map<String, Object>>
    inscrire(
            @RequestBody User user) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            User savedUser =
                    userService.inscrire(user);

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Inscription réussie. "
                            + "Un code de confirmation "
                            + "a été envoyé à votre adresse email."
            );

            response.put(
                    "email",
                    savedUser.getEmail()
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    safeMessage(e)
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    // =====================================================
    // CONFIRMATION
    // =====================================================

    @PostMapping("/confirmer")
    public ResponseEntity<Map<String, Object>>
    confirmer(
            @RequestBody Map<String, String> payload) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            String email =
                    payload.get("email");

            String code =
                    payload.get("code");

            userService.activerCompte(
                    email,
                    code
            );

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Compte activé avec succès !"
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    safeMessage(e)
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    // =====================================================
    // RENVOYER CODE
    // =====================================================

    @PostMapping("/renvoyer-code")
    public ResponseEntity<Map<String, Object>>
    renvoyerCode(
            @RequestBody Map<String, String> payload) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            userService.generateNewCode(
                    payload.get("email")
            );

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Un nouveau code a été envoyé "
                            + "à votre adresse email."
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    safeMessage(e)
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>>
    login(
            @RequestBody Map<String, String> credentials,
            HttpServletRequest request,
            HttpServletResponse responseHttp) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            String email =
                    credentials.get("email");

            String password =
                    credentials.get("password");

            if (email == null
                    || email.isBlank()
                    || password == null
                    || password.isBlank()) {

                response.put(
                        "success",
                        false
                );

                response.put(
                        "message",
                        "Email et mot de passe obligatoires."
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            User user =
                    userService.findByEmail(
                            email
                    );

            if (!Boolean.TRUE.equals(
                    user.getActif()
            )) {

                response.put(
                        "success",
                        false
                );

                response.put(
                        "message",
                        "Compte non activé."
                );

                return ResponseEntity
                        .status(401)
                        .body(response);
            }

            if (!passwordEncoder.matches(
                    password,
                    user.getPassword()
            )) {

                response.put(
                        "success",
                        false
                );

                response.put(
                        "message",
                        "Email ou mot de passe incorrect."
                );

                return ResponseEntity
                        .status(401)
                        .body(response);
            }

            String role =
                    user.getRole() == null
                            ? "USER"
                            : user.getRole();

            List<SimpleGrantedAuthority>
                    authorities =
                    new ArrayList<>();

            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    )
            );

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            authorities
                    );

            SecurityContext context =
                    SecurityContextHolder
                            .createEmptyContext();

            context.setAuthentication(
                    authentication
            );

            SecurityContextHolder.setContext(
                    context
            );

            securityContextRepository.saveContext(
                    context,
                    request,
                    responseHttp
            );

            userService.updateLastLogin(
                    user.getEmail()
            );

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Connexion réussie."
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "nomUtilisateur",
                    user.getNomUtilisateur()
            );

            response.put(
                    "nom",
                    user.getNom()
                            + " "
                            + user.getPrenom()
            );

            response.put(
                    "role",
                    role
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    "Email ou mot de passe incorrect."
            );

            return ResponseEntity
                    .status(401)
                    .body(response);
        }
    }

    // =====================================================
    // UTILISATEUR CONNECTÉ
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>>
    me() {

        Map<String, Object> response =
                new HashMap<>();

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || "anonymousUser".equals(
                        auth.getPrincipal()
                )) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    "Utilisateur non connecté."
            );

            return ResponseEntity
                    .status(401)
                    .body(response);
        }

        try {

            User user =
                    userService.findByEmail(
                            auth.getName()
                    );

            response.put(
                    "success",
                    true
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "nomUtilisateur",
                    user.getNomUtilisateur()
            );

            response.put(
                    "nom",
                    user.getNom()
                            + " "
                            + user.getPrenom()
            );

            response.put(
                    "role",
                    user.getRole()
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    "Session invalide."
            );

            return ResponseEntity
                    .status(401)
                    .body(response);
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>>
    logout(
            HttpServletRequest request) {

        SecurityContextHolder.clearContext();

        var session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Déconnexion réussie."
        );

        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // MOT DE PASSE OUBLIÉ
    // =====================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>>
    forgotPassword(
            @RequestBody Map<String, String> payload) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            userService.generateResetToken(
                    payload.get("email")
            );

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Si cette adresse existe, "
                            + "un email de réinitialisation "
                            + "a été envoyé."
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            /*
             * Pour éviter de révéler si une adresse
             * existe ou non, on peut conserver une
             * réponse générique.
             */
            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Si cette adresse existe, "
                            + "un email de réinitialisation "
                            + "a été envoyé."
            );

            return ResponseEntity.ok(
                    response
            );
        }
    }

    // =====================================================
    // RESET PASSWORD
    // =====================================================

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>>
    resetPassword(
            @RequestBody Map<String, String> payload) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            userService.resetPassword(
                    payload.get("email"),
                    payload.get("token"),
                    payload.get("newPassword")
            );

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Mot de passe réinitialisé "
                            + "avec succès !"
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            response.put(
                    "success",
                    false
            );

            response.put(
                    "message",
                    safeMessage(e)
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    private String safeMessage(
            Exception exception) {

        String message =
                exception.getMessage();

        if (message == null
                || message.isBlank()) {

            return "Une erreur est survenue.";
        }

        return message;
    }
}