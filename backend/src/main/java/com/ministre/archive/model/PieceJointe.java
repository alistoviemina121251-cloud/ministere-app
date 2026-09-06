package com.ministre.archive.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pieces_jointes")
public class PieceJointe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nom;
    private String type;
    private Long taille;
    private String chemin;
    private LocalDateTime dateUpload;
    @ManyToOne @JoinColumn(name = "marche_id") private Marche marche;

    @PrePersist protected void onCreate() { dateUpload = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }
    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }
    public Marche getMarche() { return marche; }
    public void setMarche(Marche marche) { this.marche = marche; }
}
