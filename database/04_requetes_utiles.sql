-- =============================================================================
--  LDNC - Requêtes utiles (consultation de la base, aucune modification)
--  À lancer une par une dans pgAdmin ou psql.
-- =============================================================================

SET client_encoding = 'UTF8';

-- 1. Catalogue complet : prix TTC calculé, marque et catégorie (jointures externes)
SELECT p.id, p.nom, m.nom AS marque, c.nom AS categorie,
       p.prix_ht, ROUND(CAST(p.prix_ht * (1 + p.tva / 100) AS NUMERIC), 2) AS prix_ttc, p.stock
FROM produit p
LEFT JOIN marque m    ON m.id = p.marque_id
LEFT JOIN categorie c ON c.id = p.categorie_id
ORDER BY c.nom, p.nom;

-- 2. Recherche d'un produit (comme la barre de recherche du site)
SELECT id, nom, prix_ht
FROM produit
WHERE LOWER(nom) LIKE '%ryzen%' OR LOWER(description) LIKE '%ryzen%';

-- 3. Comptes et leurs rôles
SELECT u.email, u.prenom, u.nom, r.nom AS role, u.date_creation
FROM users u
JOIN user_role ur ON ur.user_id = u.id
JOIN role r       ON r.id = ur.role_id
ORDER BY u.id;

-- 4. Contenu des paniers avec le sous-total TTC de chaque ligne
SELECT u.email, p.nom AS produit, lp.quantite,
       ROUND(CAST(p.prix_ht * (1 + p.tva / 100) * lp.quantite AS NUMERIC), 2) AS sous_total_ttc
FROM ligne_panier lp
JOIN panier pa  ON pa.id = lp.panier_id
JOIN users u    ON u.id = pa.user_id
JOIN produit p  ON p.id = lp.produit_id
ORDER BY u.email;

-- 5. Total TTC de chaque panier (regroupement)
SELECT u.email, SUM(lp.quantite) AS nb_articles,
       ROUND(CAST(SUM(p.prix_ht * (1 + p.tva / 100) * lp.quantite) AS NUMERIC), 2) AS total_ttc
FROM ligne_panier lp
JOIN panier pa ON pa.id = lp.panier_id
JOIN users u   ON u.id = pa.user_id
JOIN produit p ON p.id = lp.produit_id
GROUP BY u.email;

-- 6. Valeur du stock par catégorie, grâce à la vue (voir 02_sql_avance.sql)
SELECT * FROM vue_stock_par_categorie ORDER BY valeur_stock_ht DESC;

-- 7. Produits à réapprovisionner, grâce à la vue du catalogue
SELECT nom, marque, stock, etat_stock
FROM vue_catalogue
WHERE etat_stock <> 'En stock'
ORDER BY stock;

-- -----------------------------------------------------------------------------
--  Exemples d'appel des procédures stockées (ATTENTION : elles modifient la base)
-- -----------------------------------------------------------------------------
-- CALL reapprovisionner(4, 10);                 -- 10 MSI RTX 4070 livrées
-- CALL supprimer_compte('lea.martin@test.nc');  -- demande d'effacement RGPD
