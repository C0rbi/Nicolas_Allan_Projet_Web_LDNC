# LDNC - Cahier de recette

Recette réalisée le 11/11/2026 sur les versions 1.0.0 et 1.1.0 : API sur PostgreSQL 18, site dans Chrome.
Données de départ : scripts `01_structure.sql`, `02_sql_avance.sql` et `03_donnees.sql`.

## 1. Tests automatisés

| Commande | Résultat |
|---|---|
| `.\mvnw.cmd test` (dossier `backend`) | 24 tests, 0 échec |
| `npx ng test --watch=false` (dossier `webapp`) | 5 tests, 0 échec |
| `npx ng build` (dossier `webapp`) | Compilation sans erreur ni avertissement |

## 2. Scénarios manuels

| N° | Fonction | Scénario | Résultat attendu | Résultat |
|---|---|---|---|---|
| R01 | Catalogue | Ouvrir « Tous les produits » | 15 produits, prix TTC au format 1 380,00 € | Conforme |
| R02 | Recherche | Taper « ryzen » dans la barre de recherche | 2 produits : Ryzen 5 9600X et Ryzen 7 9800X3D | Conforme |
| R03 | Recherche | Taper « RYZEN » (majuscules) | Mêmes résultats que R02 | Conforme |
| R04 | Filtre | Catégorie « Composants » | 10 produits (sous-catégories comprises) | Conforme |
| R05 | Filtre | Composants + prix maximum 300 € | 5 produits, tous à 300 € TTC ou moins | Conforme |
| R06 | Tri | Recherche « ryzen », tri « Prix décroissant » | Ryzen 7 (660 €) avant Ryzen 5 (276 €) | Conforme |
| R07 | Fiche produit | Ouvrir l'écran ASUS TUF (stock 3) | Prix TTC 252 €, HT 210 €, « Plus que 3 en stock », quantités 1 à 3 | Conforme |
| R08 | Rupture | Afficher la MSI RTX 4070 (stock 0) | « Rupture de stock », bouton d'ajout grisé | Conforme |
| R09 | Panier (visiteur) | Visiteur : clic sur « Ajouter au panier » | Redirection vers la connexion, puis retour sur la page après connexion | Conforme |
| R10 | Connexion | Bon email, mauvais mot de passe | Message « Email ou mot de passe incorrect » | Conforme |
| R11 | Connexion | Compte client de démonstration | Prénom affiché en haut à droite, panier du compte chargé | Conforme |
| R12 | Panier | Ajouter 2 produits depuis la liste | La pastille du panier augmente de 2, message « Ajouté au panier » | Conforme |
| R13 | Panier | Boutons + et − | Quantité, sous-total et totaux recalculés | Conforme |
| R14 | Panier | Bouton « Retirer » | La ligne disparaît, totaux et pastille mis à jour | Conforme |
| R15 | Stock | Fiche écran (stock 3) : ajouter 2 puis encore 2 | 2e ajout refusé : « Stock insuffisant : il reste 3 exemplaire(s) » | Conforme |
| R16 | Déconnexion | « Se déconnecter » | Retour à l'accueil, token effacé, plus de pastille | Conforme |
| R17 | Accès | Visiteur : ouvrir /panier | Redirection vers la page de connexion | Conforme |
| R18 | Accès | Client ou visiteur : ouvrir /admin | Redirection vers l'accueil | Conforme |
| R19 | Inscription | Mot de passe de 5 caractères | Message sous le champ, bouton désactivé | Conforme |
| R20 | Inscription | Formulaire valide, conditions cochées | Compte créé (rôle CLIENT), utilisateur connecté | Conforme |
| R21 | Inscription | Email déjà utilisé (même en majuscules) | « Un compte existe déjà avec cet email » | Conforme |
| R22 | Admin | Connexion administrateur | Lien « Gestion » visible, liste des 15 produits | Conforme |
| R23 | Admin | Modifier le stock du Corsair K70 (6 → 12) | Nouvelle valeur dans la liste et dans le catalogue | Conforme |
| R24 | Admin | Ajouter un produit (prix HT 149,99, TVA 20 %) | Prix TTC 179,99 € calculé, produit présent dans la liste | Conforme |
| R25 | Admin | Supprimer ce produit puis confirmer | Message de confirmation, produit retiré | Conforme |
| R26 | Sécurité API | Client connecté : `POST /api/produits` | Réponse 403 (interdit) | Conforme |
| R27 | Sécurité API | Sans token : `GET /api/panier` | Réponse 401 (non connecté) | Conforme |
| R28 | Sécurité API | Token modifié à la main | Réponse 401 sur le panier ; le catalogue reste accessible | Conforme |
| R29 | Sécurité API | Appel depuis une autre origine (CORS) | Requête refusée (403) | Conforme |
| R30 | Base de données | Mot de passe d'un compte dans la table `users` | Empreinte BCrypt (`$2a$10$...`), jamais le mot de passe | Conforme |
| R31 | Base de données | `INSERT` direct d'une ligne de panier au-delà du stock | Refusé par le déclencheur : « Stock insuffisant » | Conforme |
| R32 | Affichage | Largeur téléphone (390 px) | Recherche sous le logo, filtres au-dessus de la liste | Conforme |
| R33 | Vue SQL | `SELECT * FROM vue_catalogue WHERE etat_stock <> 'En stock'` | 4 produits : 1 en rupture, 3 en stock faible, prix TTC calculé | Conforme |
| R34 | Vue SQL + site | Page Gestion, tableau « Stock par catégorie » | 8 catégories, mêmes valeurs que `vue_stock_par_categorie` (ex. Processeurs : 3 produits, 18 270 € HT) | Conforme |
| R35 | Sécurité API | Client connecté : `GET /api/statistiques` | Réponse 403 (réservé aux administrateurs) | Conforme |
| R36 | Déclencheur 2 | Panier avec 3 écrans, puis stock de l'écran ramené à 2 | La quantité du panier passe à 2 | Conforme |
| R37 | Déclencheur 2 | Stock d'un produit du panier ramené à 0 | La ligne est retirée du panier ; une hausse de stock ne change rien | Conforme |
| R38 | Procédure 1 | `CALL reapprovisionner(4, 10)` puis quantité 0 et produit 999 | Stock de la RTX 4070 à 10 ; les deux appels invalides sont refusés avec un message | Conforme |
| R39 | Procédure 2 | `CALL supprimer_compte('client@...')` puis compte admin et email inconnu | Compte, panier et lignes supprimés ; admin refusé ; email inconnu signalé | Conforme |

## 3. Anomalies corrigées pendant la recette

| Anomalie | Cause | Correction |
|---|---|---|
| Un client connecté recevait 401 au lieu de 403 en appelant une route d'administration | La page d'erreur interne de Spring (`/error`) était elle-même protégée | `/error` autorisée dans `SecurityConfig` |
| La compilation échouerait sous Linux (GitHub Actions, autre poste) | Fichiers enregistrés dans Git sous les noms `produitController.java` et `produitRepository.java` | Renommage avec `git mv` en `ProduitController.java` et `ProduitRepository.java` |
| Sur la fiche produit, l'ancien message « ajouté au panier » restait affiché à côté d'une erreur de stock | Le message de succès n'était pas effacé avant un nouvel ajout | Remise à zéro des deux messages avant chaque ajout |
