package com.spirit.Citizen.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nom;
    private String prenom;
    private String sujet;
    private String message;
    private String numero;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL)
    private List<Preuve> preuves;

    @Embedded
    private Localisation localisation;

    @Enumerated(EnumType.STRING)
    private Statut statut;

}

