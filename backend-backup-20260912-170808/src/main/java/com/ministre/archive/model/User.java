package com.ministre.archive.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(
            name = "idx_user_email",
            columnList = "email"
        ),
        @Index(
            name = "idx_user_username",
            columnList = "nom_utilisateur"
        )
    }
)
public class User {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(
        name = "nom_utilisateur",
        nullable = false,
        unique = true
    )
    private String nomUtilisateur;

    @Column(
        nullable = false,
        unique = true
    )
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "code_confirmation")
    private String codeConfirmation;

    @Column(name = "code_expiration")
    private LocalDateTime codeExpiration;

    @Column(nullable = false)
    private Boolean actif = false;

    private String role;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(
            String nomUtilisateur) {

        this.nomUtilisateur =
                nomUtilisateur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(
            LocalDateTime dateInscription) {

        this.dateInscription =
                dateInscription;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(
            LocalDateTime derniereConnexion) {

        this.derniereConnexion =
                derniereConnexion;
    }

    public String getCodeConfirmation() {
        return codeConfirmation;
    }

    public void setCodeConfirmation(
            String codeConfirmation) {

        this.codeConfirmation =
                codeConfirmation;
    }

    public LocalDateTime getCodeExpiration() {
        return codeExpiration;
    }

    public void setCodeExpiration(
            LocalDateTime codeExpiration) {

        this.codeExpiration =
                codeExpiration;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(
            LocalDateTime resetTokenExpiry) {

        this.resetTokenExpiry =
                resetTokenExpiry;
    }
}