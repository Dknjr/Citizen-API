package com.spirit.Citizen.services.user;

import com.spirit.Citizen.dto.DemandeDto;
import com.spirit.Citizen.model.*;
import com.spirit.Citizen.repository.DemandeRepository;
import com.spirit.Citizen.repository.PreuveRepository;
import com.spirit.Citizen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private NotificationServices notificationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PreuveRepository preuveRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoogleCloudStorageService googleCloudStorageService;

    public Demande faireDemande(Demande demande, Long userId, List<MultipartFile> fichiersPreuves) {
        // Trouver l'utilisateur par son ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + userId));

        // Associer l'utilisateur à la demande
        demande.setUser(user);
        demande.setStatut(Statut.RECU);
        demande.setCreateAt(LocalDateTime.now());

        // Sauvegarder la nouvelle demande
        Demande nouvelleDemande = demandeRepository.save(demande);

        // Liste des preuves
        List<Preuve> preuves = new ArrayList<>();
        for (MultipartFile fichier : fichiersPreuves) {
            try {
                String lienFichier = googleCloudStorageService.uploadFile(fichier);

                Preuve preuve = new Preuve();
                preuve.setCheminPhoto(lienFichier);
                preuve.setDemande(nouvelleDemande); // Associer la preuve à la nouvelle demande

                preuves.add(preuve);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'upload du fichier : " + fichier.getOriginalFilename(), e);
            }
        }

        // Sauvegarder les preuves dans la base de données
        if (!preuves.isEmpty()) {
            preuveRepository.saveAll(preuves);
        }

        notificationService.envoyerNotificationDemnde(
                "Votre demande a bien ete enregistrer: " + nouvelleDemande.getSujet(),
                user,
                nouvelleDemande
        );

        // Envoyer un email de réception de la demande
        try {
            emailService.sendDemandeReceptionEmail(user.getEmail(), demande);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de réception.", e);
        }

        // Envoyer une notification à tous les administrateurs
        List<User> admins = userRepository.findByUserRole(UserRole.ADMIN);
        if (admins.isEmpty()) {
            throw new IllegalStateException("Aucun administrateur trouvé.");
        }
        for (User admin : admins) {
            notificationService.envoyerNotificationDemnde(
                    "Une nouvelle demande a été créée : " + nouvelleDemande.getSujet(),
                    admin,
                    nouvelleDemande
            );
        }


        return nouvelleDemande;
    }

    public Demande approuveeDemande(Long demandeId){
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isPresent()){
            Demande demande = demandeOpt.get();
            demande.setStatut(Statut.APPROUVEE);
            demande.setUpdateAt(LocalDateTime.now());

            try {
                emailService.sendDemandeAppouverEmail(demande.getUser().getEmail(), demande);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Erreur lors de l'envoi de l'email de traitement.", e);
            }

            notificationService.envoyerNotificationDemnde(
                    "Votre demande a été Approuvé et est désormais en traitement.",
                    demande.getUser(),
                    demande
            );

            return  demandeRepository.save(demande);
        }
        return null;
    }

    public Demande rejetDemande(Long demandeId){
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isPresent()){
            Demande demande = demandeOpt.get();
            demande.setStatut(Statut.REJETE);
            demande.setUpdateAt(LocalDateTime.now());

            try {
                emailService.sendDemandeAppouverEmail(demande.getUser().getEmail(), demande);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Erreur lors de l'envoi de l'email de traitement.", e);
            }

            notificationService.envoyerNotificationDemnde(
                    "Votre demande a été rejeté.",
                    demande.getUser(),
                    demande
            );

            return  demandeRepository.save(demande);
        }
        return null;
    }

    private static DateTimeFormatter FrenchDate = DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm");
    public static DemandeDto convertToDto(Demande demande) {
        if (demande == null) {
            return null;
        }

        DemandeDto dto = new DemandeDto();
        dto.setId(demande.getId());
        dto.setEmail(demande.getEmail());
        dto.setNom(demande.getNom());
        dto.setPrenom(demande.getPrenom());
        dto.setNumero(demande.getNumero());
        dto.setSujet(demande.getSujet());
        dto.setMessage(demande.getMessage());
        String formatFrenchDate = demande.getCreateAt() != null
                ? demande.getCreateAt().format(FrenchDate)
                : "N/A";
        dto.setCreateAt(formatFrenchDate);
        dto.setDemanderPar(demande.getUser() != null ? demande.getUser().getProfil().getPseudo() : null);
        dto.setPreuves(demande.getPreuves().stream().map(Preuve::getCheminPhoto).collect(Collectors.toList()));
        dto.setLocalisation(demande.getLocalisation());
        dto.setStatut(demande.getStatut());

        return dto;
    }
    public static List<DemandeDto> convertDemandeListToDto(List<Demande> demandes) {
        return demandes.stream()
                .map(DemandeService::convertToDto)
                .collect(Collectors.toList());
    }
    public List<DemandeDto> getAllDemandes() {
        List<Demande> demandes = demandeRepository.findAll();
        return convertDemandeListToDto(demandes);
    }

    public List<DemandeDto> getDemandesByUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        List<Demande> demandes = demandeRepository.findByUser(user);
        return convertDemandeListToDto(demandes);
    }
}
