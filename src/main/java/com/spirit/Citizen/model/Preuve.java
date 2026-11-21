package com.spirit.Citizen.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Preuve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cheminPhoto;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private Demande demande;

    @ManyToOne
    @JoinColumn(name = "signalement_id")
    private Signalement signalement;

    // Getters et Setters
}

