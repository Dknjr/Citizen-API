package com.spirit.Citizen.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User utilisateur;

    private LocalDateTime createAt;
    private boolean lu;

    @ManyToOne
    @JoinColumn(name = "signalement_id", nullable = true)
    @JsonIgnore
    private Signalement signalement;

    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = true)
    @JsonIgnore
    private Demande demande;
}
