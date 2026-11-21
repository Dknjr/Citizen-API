package com.spirit.Citizen.dto;

import com.spirit.Citizen.model.Localisation;
import com.spirit.Citizen.model.Preuve;
import com.spirit.Citizen.model.Statut;
import com.spirit.Citizen.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DemandeDto {

    private Long id;

    private String email;
    private String nom;
    private String prenom;
    private String numero;
    private String sujet;
    private String message;

    private String createAt;
    private String demanderPar;

    private List<String> preuves;
    private Localisation localisation;
    private Statut statut;
}
