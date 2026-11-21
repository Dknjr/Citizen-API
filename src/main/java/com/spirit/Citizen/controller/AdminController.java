package com.spirit.Citizen.controller;

import com.spirit.Citizen.dto.DemandeDto;
import com.spirit.Citizen.dto.SignalementDto;
import com.spirit.Citizen.dto.Statistiques;
import com.spirit.Citizen.model.*;
import com.spirit.Citizen.services.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private NotificationServices notificationService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private SignalementService signalementService;
    @Autowired
    private StatistiquesService statistiquesService;

    ////////////////////////////// Notification ///////////////////////////////////////////////////
    @GetMapping("/allnotifiation-byuser")
    public List<Notification> getAllNotifications(@RequestParam Long userId) {
        User utilisateur = userService.findUserById(userId);
        return notificationService.getAllNotifications(utilisateur);
    }

    @GetMapping("/all-unread-notification-byuser")
    public List<Notification> getAllUnreadNotifications(@RequestParam Long userId) {
        User utilisateur = userService.findUserById(userId);
        return notificationService.getAllUnreadNotifications(utilisateur);
    }

    @PostMapping("/mark-notification-as-read/{notificationId}")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }

    @PostMapping("/mark-all-notification-as-read")
    public void markAllNotificationsAsRead(@RequestParam Long userId) {
        User utilisateur = userService.findUserById(userId);
        notificationService.markAllNotificationsAsRead(utilisateur);
    }


    ////////////////////////////////////////////////////////////////////////////////////////


    // Approuver une demande
    @PutMapping("/approuverdemande/{demandeId}")
    public ResponseEntity<Demande> approuverDemande(@PathVariable Long demandeId) {
        Demande demande = demandeService.approuveeDemande(demandeId);
        if (demande != null) {
            return ResponseEntity.ok(demande);
        }
        return ResponseEntity.notFound().build();
    }

    // Rejeter une demande
    @PutMapping("/rejeterdemande/{demandeId}")
    public ResponseEntity<Demande> rejeterDemande(@PathVariable Long demandeId) {
        Demande demande = demandeService.rejetDemande(demandeId);
        if (demande != null) {
            return ResponseEntity.ok(demande);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("all-demande")
    public ResponseEntity<List<DemandeDto>> getAllDemandes() {
        List<DemandeDto> demandes = demandeService.getAllDemandes();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }

    @GetMapping("all-signalement")
    public ResponseEntity<List<SignalementDto>> getAllSignalements() {
        List<SignalementDto> signalements = signalementService.getAllSignalements();
        return new ResponseEntity<>(signalements, HttpStatus.OK);
    }

//    @PutMapping("/{id}/statut")
//    public ResponseEntity<Signalement> updateStatut(@PathVariable Long id, @RequestParam Statut statut) {
//        try {
//            Signalement updatedSignalement = signalementService.updateStatut(id, statut);
//            return ResponseEntity.ok(updatedSignalement);
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
    @PutMapping("/traiterSignalement/{id}")
    public ResponseEntity<Signalement> traiterSignalement(@PathVariable Long id) {
        Signalement signalement = signalementService.traiterSignalement(id);
        if (signalement != null) {
            return ResponseEntity.ok(signalement);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/terminer-signalement/{id}")
    public ResponseEntity<Signalement> terminerTraitement(@PathVariable Long id) {
        Signalement signalement = signalementService.terminerTraitement(id);
        if (signalement != null) {
            return ResponseEntity.ok(signalement);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/simple-stats")
    public Statistiques getStatistiques() {
        return statistiquesService.getStatistiques();
    }
    @GetMapping("/stat-byMois")
    public Statistiques getStatistiques(@RequestParam int mois, @RequestParam int annee) {
        return statistiquesService.getStatistiquesMois(mois, annee);
    }

}

