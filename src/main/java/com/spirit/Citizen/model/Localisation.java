package com.spirit.Citizen.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Localisation {
    private Double latitude;
    private Double longitude;

//    public double distanceTo(Localisation autre) {
//        double earthRadius = 6371000;
//        double dLat = Math.toRadians(autre.latitude - this.latitude);
//        double dLng = Math.toRadians(autre.longitude - this.longitude);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(autre.latitude)) *
//                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return earthRadius * c;
//    }
    /*
        Cette méthode calcule la distance entre deux points géographiques (this et autre)
        en utilisant la formule de Haversine, qui prend en compte la courbure de la Terre.
        Elle renvoie la distance en kilomètres en fonction des latitudes et longitudes des points.
    */
    public double distanceTo(Localisation autre) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(autre.latitude - this.latitude);
        double dLng = Math.toRadians(autre.longitude - this.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(autre.latitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

}

