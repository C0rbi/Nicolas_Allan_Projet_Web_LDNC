-- =============================================================================
--  LDNC - SQL avancé : 2 vues, 2 déclencheurs (triggers), 2 procédures stockées
--
--  À exécuter APRÈS 01_structure.sql (et avant 03_donnees.sql) :
--      psql -U postgres -d ldncdb -f database/02_sql_avance.sql
--  Le script peut être relancé sans risque : il ne supprime aucune donnée.
-- =============================================================================

SET client_encoding = 'UTF8';
SET client_min_messages = warning;


-- -----------------------------------------------------------------------------
--  VUES : des requêtes enregistrées, qu'on interroge comme des tables
-- -----------------------------------------------------------------------------

-- Vue 1 : le catalogue prêt à lire (prix TTC calculé, marque, catégorie, état du stock)
--     SELECT * FROM vue_catalogue WHERE etat_stock = 'Rupture de stock';
CREATE OR REPLACE VIEW vue_catalogue AS
SELECT p.id,
       p.nom,
       m.nom AS marque,
       c.nom AS categorie,
       p.prix_ht,
       ROUND(CAST(p.prix_ht * (1 + p.tva / 100) AS NUMERIC), 2) AS prix_ttc,
       p.stock,
       CASE
           WHEN p.stock = 0 THEN 'Rupture de stock'
           WHEN p.stock <= 5 THEN 'Stock faible'
           ELSE 'En stock'
       END AS etat_stock
FROM produit p
LEFT JOIN marque m    ON m.id = p.marque_id
LEFT JOIN categorie c ON c.id = p.categorie_id;

-- Vue 2 : le stock par catégorie. Utilisée par le site : page Gestion (API /api/statistiques)
CREATE OR REPLACE VIEW vue_stock_par_categorie AS
SELECT c.nom                                              AS categorie,
       COUNT(p.id)                                        AS nb_produits,
       SUM(CASE WHEN p.stock = 0 THEN 1 ELSE 0 END)       AS nb_en_rupture,
       SUM(p.stock)                                       AS nb_articles,
       ROUND(CAST(SUM(p.prix_ht * p.stock) AS NUMERIC), 2) AS valeur_stock_ht
FROM categorie c
JOIN produit p ON p.categorie_id = c.id
GROUP BY c.nom
ORDER BY c.nom;


-- -----------------------------------------------------------------------------
--  DÉCLENCHEURS (TRIGGERS) : s'exécutent tout seuls quand une table est modifiée
-- -----------------------------------------------------------------------------

-- Déclencheur 1 : la base refuse une quantité de panier supérieure au stock,
-- même si la ligne est ajoutée sans passer par l'API (deuxième sécurité).
CREATE OR REPLACE FUNCTION verifier_stock_panier() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantite > (SELECT stock FROM produit WHERE id = NEW.produit_id) THEN
        RAISE EXCEPTION 'Stock insuffisant pour le produit %', NEW.produit_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_verifier_stock_panier ON ligne_panier;
CREATE TRIGGER trg_verifier_stock_panier
    BEFORE INSERT OR UPDATE ON ligne_panier
    FOR EACH ROW EXECUTE FUNCTION verifier_stock_panier();

-- Déclencheur 2 : quand le stock d'un produit baisse (ex. l'administrateur le corrige),
-- les paniers sont ajustés : quantité ramenée au stock restant, ligne retirée en cas de rupture.
CREATE OR REPLACE FUNCTION ajuster_paniers_au_stock() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.stock = 0 THEN
        DELETE FROM ligne_panier WHERE produit_id = NEW.id;
    ELSE
        UPDATE ligne_panier SET quantite = NEW.stock
        WHERE produit_id = NEW.id AND quantite > NEW.stock;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_ajuster_paniers_au_stock ON produit;
CREATE TRIGGER trg_ajuster_paniers_au_stock
    AFTER UPDATE OF stock ON produit
    FOR EACH ROW
    WHEN (NEW.stock < OLD.stock)
    EXECUTE FUNCTION ajuster_paniers_au_stock();


-- -----------------------------------------------------------------------------
--  PROCÉDURES STOCKÉES : des traitements enregistrés dans la base, lancés avec CALL
-- -----------------------------------------------------------------------------

-- Procédure 1 : réapprovisionner un produit à l'arrivée d'une livraison
--     CALL reapprovisionner(4, 10);   -- 10 cartes graphiques MSI RTX 4070 livrées
CREATE OR REPLACE PROCEDURE reapprovisionner(p_produit_id INTEGER, p_quantite INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_quantite <= 0 THEN
        RAISE EXCEPTION 'La quantité livrée doit être positive';
    END IF;
    UPDATE produit SET stock = stock + p_quantite WHERE id = p_produit_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Produit % introuvable', p_produit_id;
    END IF;
END;
$$;

-- Procédure 2 : droit à l'effacement (RGPD). Supprime un compte client et ses données :
-- son panier et ses lignes disparaissent grâce aux ON DELETE CASCADE.
--     CALL supprimer_compte('lea.martin@test.nc');
CREATE OR REPLACE PROCEDURE supprimer_compte(p_email VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1
               FROM users u
               JOIN user_role ur ON ur.user_id = u.id
               JOIN role r       ON r.id = ur.role_id
               WHERE u.email = LOWER(p_email) AND r.nom = 'ADMIN') THEN
        RAISE EXCEPTION 'Un compte administrateur ne peut pas être supprimé par cette procédure';
    END IF;
    DELETE FROM users WHERE email = LOWER(p_email);
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Aucun compte avec l''email %', p_email;
    END IF;
END;
$$;
