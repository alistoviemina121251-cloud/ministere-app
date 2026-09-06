package com.ministre.archive.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marches")
public class Marche {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String reference;
    private String objet;
    private String description;
    private Double montant;
    private String duree;
    private String region;
    private String departement;
    private String commune;
    private String lieuExecution;
    private String direction;
    @Enumerated(EnumType.STRING) private TypeMarche type;
    @Enumerated(EnumType.STRING) private StatutArchive statut;
    @Column(name = "date_creation") private LocalDateTime dateCreation;
    @Column(name = "date_modification") private LocalDateTime dateModification;

    @PrePersist protected void onCreate() { dateCreation = LocalDateTime.now(); dateModification = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { dateModification = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getObjet() { return objet; }
    public void setObjet(String objet) { this.objet = objet; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public String getDuree() { return duree; }
    public void setDuree(String duree) { this.duree = duree; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }
    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }
    public String getLieuExecution() { return lieuExecution; }
    public void setLieuExecution(String lieuExecution) { this.lieuExecution = lieuExecution; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public TypeMarche getType() { return type; }
    public void setType(TypeMarche type) { this.type = type; }
    public StatutArchive getStatut() { return statut; }
    public void setStatut(StatutArchive statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}
