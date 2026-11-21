package com.spirit.Citizen.services.user;

import com.spirit.Citizen.dto.SignalementDto;
import com.spirit.Citizen.model.*;
import com.spirit.Citizen.repository.PreuveRepository;
import com.spirit.Citizen.repository.SignalementRepository;
import com.spirit.Citizen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SignalementService {

    @Autowired
    private SignalementRepository signalementRepository;
    @Autowired
    private NotificationServices notificationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PreuveRepository preuveRepository;
    @Autowired
    private GoogleCloudStorageService googleCloudStorageService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;


    public Signalement creerSignalement(Signalement signalement, Long userId, List<MultipartFile> fichiersPreuves) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + userId));

        Optional<Signalement> signalementExistant = trouverSignalementExistant(signalement);

        if (signalementExistant.isPresent()) {
            Signalement existant = signalementExistant.get();
            existant.getUsers().add(user);
            existant.setStatut(Statut.RECU);  // Mettre à jour le statut à RECU
            signalementRepository.save(existant);

            notificationService.envoyerNotification(
                    "Votre signalement a bien été enregistré et sera traité ultérieurement : " + signalement.getDescription(),
                    user,
                    existant
            );

            return existant;
        } else {
            initialiserSignalement(signalement, user);

            Signalement nouveauSignalement = signalementRepository.save(signalement);

            if (fichiersPreuves != null && !fichiersPreuves.isEmpty()) {
                List<Preuve> preuves = traiterPreuves(fichiersPreuves, nouveauSignalement);
                preuveRepository.saveAll(preuves);
            }

            notificationService.envoyerNotification(
                    "Votre signalement a bien été enregistré et sera traité ultérieurement : " + signalement.getDescription(),
                    user,
                    signalement
            );

            envoyerNotifications(nouveauSignalement, user);

            return nouveauSignalement;
        }
    }
    private void initialiserSignalement(Signalement signalement, User user) {
        signalement.setUser(user);
        signalement.setStatut(Statut.RECU);
        signalement.setCreateAt(LocalDateTime.now());
    }

    private List<Preuve> traiterPreuves(List<MultipartFile> fichiersPreuves, Signalement signalement) {
        List<Preuve> preuves = new ArrayList<>();
        for (MultipartFile fichier : fichiersPreuves) {
            String lienFichier = uploaderPreuve(fichier);
            Preuve preuve = new Preuve();
            preuve.setCheminPhoto(lienFichier);
            preuve.setSignalement(signalement);
            preuves.add(preuve);
        }
        return preuves;
    }
    private String uploaderPreuve(MultipartFile fichier) {
        try {
            return googleCloudStorageService.uploadFile(fichier);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier : " + fichier.getOriginalFilename(), e);
        }
    }

    private void envoyerNotifications(Signalement signalement, User user) {
        // Envoyer l'email de réception du signalement
        try {
            emailService.sendSignalementReceptionEmail(user.getEmail(), signalement);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de réception.", e);
        }

        // Envoyer une notification à tous les administrateurs
        List<User> admins = userRepository.findByUserRole(UserRole.ADMIN);
        if (admins.isEmpty()) {
            throw new IllegalStateException("Aucun administrateur trouvé.");
        }
        for (User admin : admins) {
            notificationService.envoyerNotification(
                    "Un nouveau signalement portant sur l'option : " + signalement.getOptionSignalement()+ " a été signalé",
                    admin,
                    signalement
            );
        }
    }

    private Optional<Signalement> trouverSignalementExistant(Signalement signalement) {
        LocalDateTime debutJournee = signalement.getCreateAt().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime finJournee = debutJournee.plusDays(1).minusSeconds(1);

        return signalementRepository.findAll().stream()
                .filter(s -> s.getCategories() == signalement.getCategories())
                .filter(s -> s.getOptionSignalement() != null && s.getOptionSignalement().equals(signalement.getOptionSignalement()))
                .filter(s -> s.getLocalisation().distanceTo(signalement.getLocalisation()) <= 100)
                .filter(s -> !s.getCreateAt().isBefore(debutJournee) && !s.getCreateAt().isAfter(finJournee))
                .findFirst();
    }

    public List<Signalement> obtenirSignalementsParUtilisateur(User user) {
        return signalementRepository.findByUser(user);
    }

    public List<Signalement> obtenirTousLesSignalements() {
        return signalementRepository.findAll();
    }

    public Signalement traiterSignalement(Long signalementId) {
        Optional<Signalement> signalementOpt = signalementRepository.findById(signalementId);
        if (signalementOpt.isPresent()) {
            Signalement signalement = signalementOpt.get();
            signalement.setStatut(Statut.EN_TRAITEMENT);
            signalement.setUpdateAt(LocalDateTime.now());

            signalement.getUsers().forEach(user -> {
                try {
                    emailService.sendSignalementEnTraitementEmail(user.getEmail(), signalement);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Erreur lors de l'envoi de l'email de traitement.", e);
                }

                notificationService.envoyerNotification(
                        "Votre signalement est actuellement en cours de traitement.",
                        user,
                        signalement
                );
            });

            return signalementRepository.save(signalement);
        }
        return null;
    }

    public Signalement terminerTraitement(Long signalementId) {
        Optional<Signalement> signalementOpt = signalementRepository.findById(signalementId);
        if (signalementOpt.isPresent()) {
            Signalement signalement = signalementOpt.get();
            signalement.setStatut(Statut.TRAITEE);
            signalement.setUpdateAt(LocalDateTime.now());

            signalement.getUsers().forEach(user -> {
                try {
                    emailService.sendSignalementTermineEmail(user.getEmail(), signalement);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Erreur lors de l'envoi de l'email de fin de traitement.", e);
                }

                notificationService.envoyerNotification(
                        "Votre signalement a été traité avec succès.",
                        user,
                        signalement
                );
            });

            return signalementRepository.save(signalement);
        }
        return null;
    }
    private static DateTimeFormatter FrenchDate = DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm");
    public static SignalementDto convertToDto(Signalement signalement) {
        SignalementDto dto = new SignalementDto();
        dto.setId(signalement.getId());
        dto.setDescription(signalement.getDescription());

        List<String> pseudos = new ArrayList<>();

        if (signalement.getUser() != null && signalement.getUser().getProfil() != null) {
            pseudos.add(signalement.getUser().getProfil().getPseudo());
        } else {
            pseudos.add("Créateur inconnu");
        }

        List<String> demanderPar = signalement.getUsers().stream()
                .map(user -> user.getProfil() != null ? user.getProfil().getPseudo() : "Inconnu")
                .collect(Collectors.toList());

        pseudos.addAll(demanderPar);

        dto.setDemanderPar(String.join(", ", pseudos));

        dto.setPreuves(signalement.getPreuves().stream().map(Preuve::getCheminPhoto).collect(Collectors.toList()));
        dto.setLocalisation(signalement.getLocalisation());
        dto.setStatut(signalement.getStatut());
        dto.setCategories(signalement.getCategories());
        String formatFrenchDate = signalement.getCreateAt() != null
                ? signalement.getCreateAt().format(FrenchDate)
                : "N/A";
        dto.setCreateAt(formatFrenchDate);
        dto.setOptionSignalement(signalement.getOptionSignalement());
        return dto;
    }

    public static List<SignalementDto> convertSignalementListToDto(List<Signalement> signalements) {
        return signalements.stream()
                .map(SignalementService::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SignalementDto> getAllSignalements() {
        List<Signalement> signalements = signalementRepository.findAll();
        return convertSignalementListToDto(signalements);
    }
    public List<SignalementDto> getSignalementsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList(); // Si l'utilisateur n'existe pas
        }
        List<Signalement> signalements = signalementRepository.findByUser(user);
        return convertSignalementListToDto(signalements);
    }

    // Passer un signalement en statut "EN_TRAITEMENT"
//    public Signalement traiterSignalement(Long id) throws Exception {
//        return updateStatut(id, Statut.EN_TRAITEMENT);
//    }

    public Signalement updateStatut(Long id, Statut statut) throws Exception {
        Optional<Signalement> signalementOpt = signalementRepository.findById(id);

        if (signalementOpt.isPresent()) {
            Signalement signalement = signalementOpt.get();
            signalement.setStatut(statut);
            signalement.setUpdateAt(LocalDateTime.now());
            return signalementRepository.save(signalement);
        } else {
            throw new Exception("Signalement non trouvé");
        }
    }


}

