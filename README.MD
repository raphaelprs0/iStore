# iStore Ltd - Gestion de Stock et Utilisateurs

## Description
Application Java de gestion d'inventaire utilisant une architecture MVC et le pattern DAO.
Projet réalisé dans le cadre du module 2JAVA à SUPINFO par Raphaël PIRES & Hippolyte BOUJU.

## Fonctionnalités Clés
- Authentification sécurisée (SHA-256 + Salt).
- Gestion des Rôles (Admin / User).
- Admin : Whitelist d'emails, Gestion complète des magasins et utilisateurs.
- User : Consultation et modification des stocks (Vente/Réception).
- Base de données locale SQLite (Fichier unique).

## Prérequis
- Java JDK 17 ou supérieur.
- Maven (pour la gestion des dépendances).
- Connexion Internet (pour le premier téléchargement du driver SQLite).

## Installation & Lancement
1. Ouvrir le projet avec IntelliJ IDEA (ou tout IDE supportant Maven).
2. Laisser Maven télécharger les dépendances (fichier pom.xml).
3. Lancer la classe principale : `main.Main`.

## Identifiants par défaut
Au premier lancement, un compte Administrateur est créé automatiquement :
- Email : admin@istore.com
- Mot de passe : admin

## Architecture Technique
- src/main/java/com/istore/config : Configuration BDD (SQLite).
- src/main/java/com/istore/model : Objets métiers (POJO).
- src/main/java/com/istore/dao : Interfaces d'accès aux données.
- src/main/java/com/istore/dao/impl : Implémentation SQL.
- src/main/java/com/istore/service : Logique métier (Sécurité).
- src/main/java/com/istore/view : Interface Graphique (Swing).