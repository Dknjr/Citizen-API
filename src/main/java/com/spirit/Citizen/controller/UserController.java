package com.spirit.Citizen.controller;

import com.spirit.Citizen.dto.DemandeDto;
import com.spirit.Citizen.dto.SignalementDto;
import com.spirit.Citizen.model.*;
import com.spirit.Citizen.services.user.DemandeService;
import com.spirit.Citizen.services.user.NotificationServices;
import com.spirit.Citizen.services.user.SignalementService;
import com.spirit.Citizen.services.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private NotificationServices notificationService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private SignalementService signalementService;


    ///////////////////////////////////// Demande /////////////////////////////////////
    // Faire une demande
    @PostMapping(value = "/faire-demande/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> creerDemande(
            @PathVariable Long userId,
            @RequestParam("email") String email,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("numero") String numero,
            @RequestParam("sujet") String sujet,
            @RequestParam("message") String message,
            @RequestParam(value = "fichiersPreuves", required = false) List<MultipartFile> fichiersPreuves) {
        System.out.println("*********************numero************" + numero);

        // Construire l'objet Demande à partir des paramètres du formulaire
        Demande demande = new Demande();
        demande.setEmail(email);
        demande.setNom(nom);
        demande.setPrenom(prenom);
        demande.setNumero(numero);
        demande.setSujet(sujet);
        demande.setMessage(message);
        demande.setCreateAt(LocalDateTime.now());

        // Appeler le service pour créer la demande
        demandeService.faireDemande(demande, userId, fichiersPreuves);

        // Retourner un message de succès
        String successMessage = "La demande a été créée avec succès.";
        return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
    }
    @GetMapping("/demandeByUser/{userId}")
    public ResponseEntity<List<DemandeDto>> getDemandesByUser(@PathVariable Long userId) {
        List<DemandeDto> demandes = demandeService.getDemandesByUser(userId);
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }

    @PostMapping(value = "/lance-signalement/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> creerSignalement(
            @PathVariable Long userId,
            @RequestParam("description") String description,
            @RequestParam("lieu") String lieu,  // Recevoir le lieu sous la forme "(latitude)"
            @RequestParam("categories") Categories categories,
            @RequestParam("optionSignalement") String optionSignalement,
            @RequestParam(value = "fichiersPreuves", required = false) List<MultipartFile> fichiersPreuves) {
        System.out.println("optionSignalement:" + optionSignalement);

        String[] coordonnees = lieu.replace("(", "").replace(")", "").split(",");
        double latitude = Double.parseDouble(coordonnees[0].trim());
        double longitude = Double.parseDouble(coordonnees[1].trim());

        // Construire l'objet Localisation à partir des coordonnées latitude et longitude
        Localisation localisation = new Localisation();
        localisation.setLatitude(latitude);
        localisation.setLongitude(longitude);

        // Construire l'objet Signalement à partir des paramètres du formulaire
        Signalement signalement = new Signalement();
        signalement.setDescription(description);
        signalement.setLocalisation(localisation); // Assignation des coordonnées au signalement
        signalement.setCategories(categories);
        signalement.setOptionSignalement(optionSignalement);
        signalement.setCreateAt(LocalDateTime.now());

        // Appeler le service pour créer le signalement avec les preuves
        signalementService.creerSignalement(signalement, userId, fichiersPreuves);

        // Retourner un message de succès
        String successMessage = "Le signalement a été créé avec succès.";
        return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
    }

    @GetMapping("/signalementbyuser/{userId}")
    public ResponseEntity<List<SignalementDto>> getSignalementsByUser(@PathVariable Long userId) {
        List<SignalementDto> signalements = signalementService.getSignalementsByUserId(userId);
        return new ResponseEntity<>(signalements, HttpStatus.OK);
    }

    ///////////////////////////////////// Notification //////////////////////////////////
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

    //////////////////////////////////////// Profile //////////////////////////////////
    @GetMapping("/userBy-id/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }


    // Endpoint pour mettre à jour le statut d'un signalement
    @PutMapping("/{id}/statut")
    public ResponseEntity<Signalement> updateStatut(@PathVariable Long id, @RequestParam Statut statut) {
        try {
            Signalement updatedSignalement = signalementService.updateStatut(id, statut);
            return ResponseEntity.ok(updatedSignalement);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
