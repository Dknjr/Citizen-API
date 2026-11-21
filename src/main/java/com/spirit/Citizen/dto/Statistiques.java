package com.spirit.Citizen.dto;

import lombok.Data;

import java.util.List;

@Data
public class Statistiques {

    private Long nombreSignalement;
    private Long nombreDemande;
    private double pourcentageSignalement;
    private double pourcentageDemande;
    private Long nouvelleAlerte;
    private Long nbreAlerteResolu;
    private Long nbreAlerteTraitement;
    private double pourcentageSignalementParSemaine;
    private double pourcentageDemandeParSemaine;
    private List<StatistiquesSemaine> statistiquesParSemaine;
}
