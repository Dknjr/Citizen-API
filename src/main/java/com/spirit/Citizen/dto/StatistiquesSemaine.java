package com.spirit.Citizen.dto;

import lombok.Data;

@Data
public class StatistiquesSemaine {
    private int semaine;
    private Long nombreSignalement;
    private Long nombreDemande;
    private double pourcentageSignalement;
    private double pourcentageDemande;
}