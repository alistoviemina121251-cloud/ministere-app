package com.ministre.archive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "marches",
        indexes = {
                @Index(
                        name = "idx_marche_reference",
                        columnList = "reference"
                ),
                @Index(
                        name = "idx_marche_date_creation",
                        columnList = "date_creation"
                )
        }
)
public class Marche {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @NotBlank(
            message = "La référence est obligatoire."
    )
    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String reference;

    @NotBlank(
            message = "L'objet du marché est obligatoire."
    )
    @Column(
            nullable = false,
            length = 500
    )
    private String objet;

    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Le montant ne peut pas être négatif."
    )
    private Double montant;

    private String duree;

    private String region;

    private String departement;

    private String commune;

    private String lieuExecution;

    private String direction;

    @Enumerated(EnumType.STRING)
    private TypeMarche type;

    @Enumerated(EnumType.STRING)
    private StatutArchive statut =
            StatutArchive.EN_ATTENTE;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        if (dateCreation == null) {
            dateCreation = now;
        }

        if (dateModification == null) {
            dateModification = now;
        }

        if (statut == null) {
            statut =
                    StatutArchive.EN_ATTENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {

        dateModification =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference =
                reference == null
                        ? null
                        : reference.trim();
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet =
                objet == null
                        ? null
                        : objet.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getLieuExecution() {
        return lieuExecution;
    }

    public void setLieuExecution(String lieuExecution) {
        this.lieuExecution = lieuExecution;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public TypeMarche getType() {
        return type;
    }

    public void setType(TypeMarche type) {
        this.type = type;
    }

    public StatutArchive getStatut() {
        return statut;
    }

    public void setStatut(StatutArchive statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(
            LocalDateTime dateCreation) {

        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(
            LocalDateTime dateModification) {

        this.dateModification =
                dateModification;
    }
}