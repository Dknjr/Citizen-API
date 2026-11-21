package com.spirit.Citizen.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "signalement")
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Lob
    private String optionSignalement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "signalement_users",
            joinColumns = @JoinColumn(name = "signalement_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "signalement", cascade = CascadeType.ALL)
    private List<Preuve> preuves;

    @Embedded
    private Localisation localisation;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @Enumerated(EnumType.STRING)
    private Categories categories;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public Signalement() {}
}
