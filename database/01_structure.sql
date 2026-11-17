-- =============================================================================
--  LDNC - Création de la base de données (PostgreSQL)
--
--  À exécuter sur la base "ldncdb", puis 02_sql_avance.sql et 03_donnees.sql :
--      psql -U postgres -d ldncdb -f database/01_structure.sql
--  (ou dans pgAdmin : clic droit sur ldncdb > Query Tool > ouvrir le fichier > Exécuter)
--
--  ATTENTION : le script supprime puis recrée toutes les tables (les données sont perdues).
-- =============================================================================

SET client_encoding = 'UTF8';   -- le fichier est en UTF-8 (accents corrects même sous Windows)
SET client_min_messages = warning;

DROP TABLE IF EXISTS ligne_panier, panier, ligne_commande, commande, adresse, avis, favoris,
    photo, produit_caracteristique, caracteristique, message, produit, marque, categorie,
    user_role, role, users CASCADE;


-- -----------------------------------------------------------------------------
--  Comptes et rôles
-- -----------------------------------------------------------------------------

CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,          -- empreinte BCrypt, jamais le mot de passe en clair
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role (
    id  SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE               -- 'ADMIN' ou 'CLIENT'
);

-- Un utilisateur peut avoir plusieurs rôles, un rôle concerne plusieurs utilisateurs
CREATE TABLE user_role (
    user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES role (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);


-- -----------------------------------------------------------------------------
--  Catalogue
-- -----------------------------------------------------------------------------

CREATE TABLE categorie (
    id        SERIAL PRIMARY KEY,
    nom       VARCHAR(100) NOT NULL UNIQUE,
    parent_id INTEGER REFERENCES categorie (id) ON DELETE SET NULL   -- ex. Processeurs est dans Composants
);

CREATE TABLE marque (
    id       SERIAL PRIMARY KEY,
    nom      VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(255)
);

CREATE TABLE produit (
    id           SERIAL PRIMARY KEY,
    nom          VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    prix_ht      DOUBLE PRECISION NOT NULL,
    tva          DOUBLE PRECISION NOT NULL DEFAULT 20,           -- taux en %
    stock        INTEGER NOT NULL DEFAULT 0,
    image_url    VARCHAR(255),                                    -- ex. produits/processeur.svg
    marque_id    INTEGER REFERENCES marque (id) ON DELETE RESTRICT,
    categorie_id INTEGER REFERENCES categorie (id) ON DELETE RESTRICT,
    date_ajout   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_prix_positif  CHECK (prix_ht >= 0),
    CONSTRAINT chk_tva_positive  CHECK (tva >= 0),
    CONSTRAINT chk_stock_positif CHECK (stock >= 0)
);


-- -----------------------------------------------------------------------------
--  Panier : un seul panier par utilisateur (UNIQUE sur user_id)
-- -----------------------------------------------------------------------------

CREATE TABLE panier (
    id      SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE
);

-- La clé primaire composée empêche d'avoir deux lignes pour le même produit dans un panier
CREATE TABLE ligne_panier (
    panier_id  INTEGER NOT NULL REFERENCES panier (id) ON DELETE CASCADE,
    produit_id INTEGER NOT NULL REFERENCES produit (id) ON DELETE CASCADE,
    quantite   INTEGER NOT NULL CHECK (quantite > 0),
    PRIMARY KEY (panier_id, produit_id)
);


-- -----------------------------------------------------------------------------
--  Tables prévues pour les prochaines versions (pas encore utilisées par le site)
-- -----------------------------------------------------------------------------

-- Galerie : plusieurs photos par produit
CREATE TABLE photo (
    id             SERIAL PRIMARY KEY,
    produit_id     INTEGER NOT NULL REFERENCES produit (id) ON DELETE CASCADE,
    url            VARCHAR(255) NOT NULL,
    est_principale BOOLEAN DEFAULT FALSE
);

-- Fiche technique : caractéristiques d'un produit (ex. "Fréquence" = "4,7 GHz")
CREATE TABLE caracteristique (
    id  SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE produit_caracteristique (
    produit_id         INTEGER NOT NULL REFERENCES produit (id) ON DELETE CASCADE,
    caracteristique_id INTEGER NOT NULL REFERENCES caracteristique (id) ON DELETE CASCADE,
    valeur             VARCHAR(255) NOT NULL,
    PRIMARY KEY (produit_id, caracteristique_id)
);

-- Avis des clients sur un produit (note de 1 à 5)
CREATE TABLE avis (
    id          SERIAL PRIMARY KEY,
    produit_id  INTEGER NOT NULL REFERENCES produit (id) ON DELETE CASCADE,
    user_id     INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    note        INTEGER NOT NULL CHECK (note BETWEEN 1 AND 5),
    commentaire TEXT,
    date_avis   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Produits favoris d'un client
CREATE TABLE favoris (
    user_id    INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    produit_id INTEGER NOT NULL REFERENCES produit (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, produit_id)
);

-- Adresses de livraison et de facturation
CREATE TABLE adresse (
    id           SERIAL PRIMARY KEY,
    user_id      INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    rue          VARCHAR(255) NOT NULL,
    code_postal  VARCHAR(20) NOT NULL,
    ville        VARCHAR(100) NOT NULL,
    type_adresse VARCHAR(50) NOT NULL              -- 'LIVRAISON' ou 'FACTURATION'
);

-- Commandes (le paiement n'est pas géré)
CREATE TABLE commande (
    id                     SERIAL PRIMARY KEY,
    user_id                INTEGER NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    reference_unique       VARCHAR(50) NOT NULL UNIQUE,
    date_commande          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut                 VARCHAR(50) DEFAULT 'EN_ATTENTE',
    total_ttc              NUMERIC(10, 2) NOT NULL,
    adresse_livraison_id   INTEGER REFERENCES adresse (id) ON DELETE RESTRICT,
    adresse_facturation_id INTEGER REFERENCES adresse (id) ON DELETE RESTRICT
);

CREATE TABLE ligne_commande (
    commande_id           INTEGER NOT NULL REFERENCES commande (id) ON DELETE CASCADE,
    produit_id            INTEGER NOT NULL REFERENCES produit (id) ON DELETE RESTRICT,
    quantite              INTEGER NOT NULL CHECK (quantite > 0),
    prix_unitaire_facture NUMERIC(10, 2) NOT NULL,  -- prix au moment de l'achat
    PRIMARY KEY (commande_id, produit_id)
);

-- Messages envoyés au service client
CREATE TABLE message (
    id            SERIAL PRIMARY KEY,
    expediteur_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    contenu       TEXT NOT NULL,
    date_envoi    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
