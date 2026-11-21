package com.spirit.Citizen.services.user;

import com.spirit.Citizen.dto.Statistiques;
import com.spirit.Citizen.dto.StatistiquesSemaine;
import com.spirit.Citizen.model.Signalement;
import com.spirit.Citizen.model.Demande;
import com.spirit.Citizen.model.Statut;
import com.spirit.Citizen.repository.SignalementRepository;
import com.spirit.Citizen.repository.DemandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatistiquesService {

    @Autowired
    private SignalementRepository signalementRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    public Statistiques getStatistiques() {
        Statistiques stats = new Statistiques();

        List<Signalement> signalements = signalementRepository.findAll();
        List<Demande> demandes = demandeRepository.findAll();

        long totalSignalements = signalements.size();
        long totalDemandes = demandes.size();
        long total = totalSignalements + totalDemandes;

        double pourcentageSignalement = (total > 0) ? ((double) totalSignalements / total) * 100 : 0;
        double pourcentageDemande = (total > 0) ? ((double) totalDemandes / total) * 100 : 0;

        long nouvelleAlerte = signalements.stream().filter(s -> s.getStatut() == Statut.RECU).count() + demandes.stream().filter(s -> s.getStatut() == Statut.RECU).count();
        long nbreAlerteResolu = signalements.stream().filter(s -> s.getStatut() == Statut.TRAITEE).count() + demandes.stream().filter(s -> s.getStatut() == Statut.TRAITEE).count();
        long nbreAlerteTraitement = signalements.stream().filter(s -> s.getStatut() == Statut.EN_TRAITEMENT).count() + demandes.stream().filter(s -> s.getStatut() == Statut.EN_TRAITEMENT).count();

        stats.setNombreSignalement(totalSignalements);
        stats.setNombreDemande(totalDemandes);
        stats.setPourcentageSignalement(pourcentageSignalement);
        stats.setPourcentageDemande(pourcentageDemande);
        stats.setNouvelleAlerte(nouvelleAlerte);
        stats.setNbreAlerteResolu(nbreAlerteResolu);
        stats.setNbreAlerteTraitement(nbreAlerteTraitement);

        return stats;
    }

    public Statistiques getStatistiquesMois(int mois, int annee) {
        Statistiques stats = new Statistiques();

        LocalDateTime debutMois = LocalDate.of(annee, mois, 1).atStartOfDay();
        LocalDateTime finMois = debutMois.plusMonths(1).minusSeconds(1);

        List<Signalement> signalements = signalementRepository.findByCreateAtBetween(debutMois, finMois);
        List<Demande> demandes = demandeRepository.findByCreateAtBetween(debutMois, finMois);

        List<StatistiquesSemaine> statistiquesParSemaine = new ArrayList<>();

        for (int semaine = 1; semaine <= 4; semaine++) {
            LocalDateTime debutSemaine = debutMois.plusWeeks(semaine - 1);
            LocalDateTime finSemaine = debutSemaine.plusWeeks(1).minusSeconds(1);

            List<Signalement> signalementsSemaine = signalements.stream()
                    .filter(s -> s.getCreateAt().isAfter(debutSemaine) && s.getCreateAt().isBefore(finSemaine))
                    .collect(Collectors.toList());

            List<Demande> demandesSemaine = demandes.stream()
                    .filter(d -> d.getCreateAt().isAfter(debutSemaine) && d.getCreateAt().isBefore(finSemaine))
                    .collect(Collectors.toList());

            long totalSignalementsSemaine = signalementsSemaine.size();
            long totalDemandesSemaine = demandesSemaine.size();
            long totalSemaine = totalSignalementsSemaine + totalDemandesSemaine;

            double pourcentageSignalementSemaine = (totalSemaine > 0) ? ((double) totalSignalementsSemaine / totalSemaine) * 100 : 0;
            double pourcentageDemandeSemaine = (totalSemaine > 0) ? ((double) totalDemandesSemaine / totalSemaine) * 100 : 0;

            StatistiquesSemaine statsSemaine = new StatistiquesSemaine();
            statsSemaine.setSemaine(semaine);
            statsSemaine.setNombreSignalement(totalSignalementsSemaine);
            statsSemaine.setNombreDemande(totalDemandesSemaine);
            statsSemaine.setPourcentageSignalement(pourcentageSignalementSemaine);
            statsSemaine.setPourcentageDemande(pourcentageDemandeSemaine);

            statistiquesParSemaine.add(statsSemaine);
        }

        long totalSignalementsMois = signalements.size();
        long totalDemandesMois = demandes.size();
        long totalMois = totalSignalementsMois + totalDemandesMois;

        double pourcentageSignalementMois = (totalMois > 0) ? ((double) totalSignalementsMois / totalMois) * 100 : 0;
        double pourcentageDemandeMois = (totalMois > 0) ? ((double) totalDemandesMois / totalMois) * 100 : 0;

        stats.setNombreSignalement(totalSignalementsMois);
        stats.setNombreDemande(totalDemandesMois);
        stats.setPourcentageSignalement(pourcentageSignalementMois);
        stats.setPourcentageDemande(pourcentageDemandeMois);
        stats.setNouvelleAlerte(signalements.stream().filter(s -> s.getStatut() == Statut.RECU).count());
        stats.setNbreAlerteResolu(signalements.stream().filter(s -> s.getStatut() == Statut.TRAITEE).count());
        stats.setNbreAlerteTraitement(signalements.stream().filter(s -> s.getStatut() == Statut.EN_TRAITEMENT).count());
        stats.setStatistiquesParSemaine(statistiquesParSemaine);

        return stats;
    }

}
