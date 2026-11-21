package com.spirit.Citizen.dto;

import com.spirit.Citizen.model.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SignalementDto {

    private Long id;
    private String description;
    private String demanderPar;
    private List<String> preuves;
    private Localisation localisation;
    private Statut statut;
    private Categories categories;
    private String createAt;
    private String optionSignalement;
}
