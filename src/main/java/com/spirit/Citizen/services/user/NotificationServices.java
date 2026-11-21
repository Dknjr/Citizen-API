package com.spirit.Citizen.services.user;

import com.spirit.Citizen.model.Demande;
import com.spirit.Citizen.model.Notification;
import com.spirit.Citizen.model.Signalement;
import com.spirit.Citizen.model.User;
import com.spirit.Citizen.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServices {

    @Autowired
    private NotificationsRepository notificationRepository;

    public void envoyerNotification(String message, User utilisateur, Signalement signalement) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateur);
        notification.setSignalement(signalement);
        notification.setCreateAt(LocalDateTime.now());
        notification.setLu(false);
        notificationRepository.save(notification);
    }

    public void envoyerNotificationDemnde(String message, User utilisateur, Demande demande) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateur);
        notification.setDemande(demande);
        notification.setCreateAt(LocalDateTime.now());
        notification.setLu(false);
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications(User utilisateur) {
        return notificationRepository.findByUtilisateur(utilisateur);
    }

    public List<Notification> getAllUnreadNotifications(User utilisateur) {
        return notificationRepository.findByUtilisateurAndLu(utilisateur, false);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setLu(true);
        notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead(User utilisateur) {
        List<Notification> notifications = notificationRepository.findByUtilisateur(utilisateur);
        for (Notification notification : notifications) {
            notification.setLu(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void envoyerNotificationDeBienvenue(User utilisateur) {
        String message = "Bienvenue " + utilisateur.getProfil().getPseudo() + "! Nous sommes ravis de vous avoir parmi nous.";
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUtilisateur(utilisateur);
        notification.setCreateAt(LocalDateTime.now());
        notification.setLu(false);

        notificationRepository.save(notification);
    }

}
