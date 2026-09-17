package com.ministre.archive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pieces_jointes",
        indexes = {
                @Index(
                        name = "idx_piece_marche",
                        columnList = "marche_id"
                )
        }
)
public class PieceJointe {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            nullable = false
    )
    private String nom;

    private String type;

    private Long taille;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String chemin;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "marche_id",
            nullable = false
    )
    @JsonIgnore
    private Marche marche;

    @PrePersist
    protected void onCreate() {

        if (dateUpload == null) {
            dateUpload =
                    LocalDateTime.now();
        }
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTaille() {
        return taille;
    }

    public void setTaille(Long taille) {
        this.taille = taille;
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(
            LocalDateTime dateUpload) {

        this.dateUpload =
                dateUpload;
    }

    public Marche getMarche() {
        return marche;
    }

    public void setMarche(Marche marche) {
        this.marche = marche;
    }
}