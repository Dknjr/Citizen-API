package com.spirit.Citizen.repository;

import com.spirit.Citizen.model.Notification;
import com.spirit.Citizen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateur(User utilisateur);
    List<Notification> findByUtilisateurAndLu(User utilisateur, boolean lu);

}
