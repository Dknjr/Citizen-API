package com.spirit.Citizen.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String pseudo;

    private String telephone;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
