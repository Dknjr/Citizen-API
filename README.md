<h1>Citizen API</h1>

<p>
  <a href="https://github.com/Dknjr/Citizen-API/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/Dknjr/Citizen-API/actions/workflows/ci.yml/badge.svg"></a>
  <img alt="Java" src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white">
  <img alt="JWT" src="https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens&logoColor=white">
</p>

API REST de **Citizen**, l'application qui permet aux habitants de la commune du Golfe 1 (Lomé) de signaler un problème dans leur quartier et de faire leurs demandes administratives, puis d'en suivre le traitement.

L'application mobile (React Native / Expo) qui consomme cette API : **[MMR-Citizen-App](https://github.com/Dknjr/MMR-Citizen-App)**.

*REST API for Citizen, a civic app where residents report local issues and file administrative requests, then track their progress.*

---

## Fonctionnalités

- **Authentification** : inscription, connexion et jeton JWT ; mots de passe hachés avec BCrypt.
- **Deux rôles** : `USER` (citoyen) et `ADMIN` (agent de la mairie), avec des routes séparées.
- **Signalements** : catégorie, description, position GPS et photos (stockées sur Google Cloud Storage). Un signalement identique (même catégorie et même type, à moins de 100 mètres, le même jour) est regroupé avec l'existant au lieu d'être dupliqué.
- **Demandes administratives** : envoi de pièces justificatives, puis approbation ou rejet par la mairie.
- **Suivi** : statuts `RECU` → `EN_TRAITEMENT` → `TRAITEE`, avec notifications dans l'app et e-mails à chaque étape.
- **Tableau de bord** : statistiques globales et par mois pour la mairie.

## Endpoints

| Méthode | Route | Rôle | Description |
|---|---|---|---|
| `POST` | `/api/auth/signup` | public | Inscription |
| `POST` | `/api/auth/login` | public | Connexion, renvoie le JWT |
| `POST` | `/api/user/lance-signalement/{userId}` | USER | Créer un signalement (multipart, avec photos) |
| `GET` | `/api/user/signalementbyuser/{userId}` | USER | Signalements d'un utilisateur |
| `POST` | `/api/user/faire-demande/{userId}` | USER | Créer une demande (multipart, avec pièces) |
| `GET` | `/api/user/demandeByUser/{userId}` | USER | Demandes d'un utilisateur |
| `GET` | `/api/user/allnotifiation-byuser?userId=` | USER | Notifications |
| `GET` | `/api/admin/all-signalement` | ADMIN | Tous les signalements |
| `PUT` | `/api/admin/traiterSignalement/{id}` | ADMIN | Passer un signalement en traitement |
| `PUT` | `/api/admin/terminer-signalement/{id}` | ADMIN | Clore un signalement |
| `GET` | `/api/admin/all-demande` | ADMIN | Toutes les demandes |
| `PUT` | `/api/admin/approuverdemande/{id}` | ADMIN | Approuver une demande |
| `PUT` | `/api/admin/rejeterdemande/{id}` | ADMIN | Rejeter une demande |
| `GET` | `/api/admin/simple-stats` | ADMIN | Statistiques globales |
| `GET` | `/api/admin/stat-byMois?mois=&annee=` | ADMIN | Statistiques d'un mois |

Les routes protégées attendent l'en-tête `Authorization: Bearer <jeton>`.

## Architecture

```
controller/   routes REST (auth, user, admin)
services/     logique métier : signalements, demandes, e-mails, stockage, statistiques, JWT
repository/   accès aux données (Spring Data JPA)
model/        entités : User, Profil, Signalement, Demande, Preuve, Notification
dto/          objets échangés avec l'application
config/       sécurité (filtre JWT, rôles), CORS, gestion des erreurs
```

## Lancer en local

Prérequis : Java 21, Maven et MySQL 8.

Toutes les valeurs sensibles sont lues dans des **variables d'environnement**, jamais dans le code :

| Variable | Obligatoire | Rôle |
|---|---|---|
| `JWT_SECRET` | oui | Clé de signature des jetons, en Base64, 32 octets minimum. Générer : `openssl rand -base64 64` |
| `DB_URL` | non | Par défaut `jdbc:mysql://localhost:3306/managment-db` |
| `DB_USERNAME`, `DB_PASSWORD` | oui | Accès MySQL |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | non | Compte administrateur créé au premier démarrage |
| `EMAIL_USERNAME`, `EMAIL_PASSWORD` | pour les e-mails | Compte SMTP (pour Gmail, un mot de passe d'application) |
| `EMAIL_HOST`, `EMAIL_PORT` | non | Par défaut `smtp.gmail.com` et `587` |
| `GCP_PROJECT_ID`, `GCP_BUCKET_NAME`, `GCP_CREDENTIALS_BASE64` | oui | Stockage des photos sur Google Cloud Storage |

```bash
export JWT_SECRET="$(openssl rand -base64 64)"
export DB_USERNAME=citizen DB_PASSWORD=...
# ... les autres variables
mvn spring-boot:run          # API sur http://localhost:2030
```

## Tests

```bash
mvn verify
```

Les tests tournent sur une base H2 en mémoire avec des valeurs factices : ni MySQL, ni Google Cloud, ni serveur mail ne sont nécessaires. La même commande est lancée par la CI sur chaque pull request.

## Auteurs

Projet réalisé à deux, en 2024, pour la mairie du Golfe 1 :

- **Herve Junior Erminio Dorkenoo** ([@Dknjr](https://github.com/Dknjr)), étudiant en MSc Intelligence Artificielle à l'ECE Paris
- **Azhar** ([@SpiritGitHub](https://github.com/SpiritGitHub))

Ce dépôt est la version publique du projet, publiée avec l'accord de l'équipe. Les identifiants ont été retirés du code et remplacés par des variables d'environnement.
