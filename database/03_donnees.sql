-- =============================================================================
--  LDNC - Données de démonstration (à exécuter APRÈS 01_structure.sql et 02_sql_avance.sql)
--      psql -U postgres -d ldncdb -f database/03_donnees.sql
--
--  Comptes de démonstration :
--      admin@ldnc.nc   / Admin2026!    (administrateur : gère les produits)
--      client@ldnc.nc  / Client2026!   (client)
-- =============================================================================

SET client_encoding = 'UTF8';   -- le fichier est en UTF-8 (accents corrects même sous Windows)

INSERT INTO role (id, nom) VALUES
    (1, 'ADMIN'),
    (2, 'CLIENT');

-- Les mots de passe sont enregistrés sous forme d'empreinte BCrypt (jamais en clair)
INSERT INTO users (id, nom, prenom, email, mot_de_passe) VALUES
    (1, 'LDNC',   'Admin', 'admin@ldnc.nc',  '$2a$10$INkBf9LhahfXN6PcT0Ldz.2aFty1v0xG3Eo21OEnumLSU0dyIzome'),
    (2, 'Dupont', 'Marie', 'client@ldnc.nc', '$2a$10$FOSvYUbXsujVOLhtL2TeUuJKL93/BF17qdY6Ho3Lc7hE6seW06Om.');

INSERT INTO user_role (user_id, role_id) VALUES
    (1, 1),
    (2, 2);

INSERT INTO categorie (id, nom, parent_id) VALUES
    (1,  'Composants',        NULL),
    (2,  'Processeurs',       1),
    (3,  'Cartes Graphiques', 1),
    (4,  'Cartes Mères',      1),
    (5,  'Mémoire Vive',      1),
    (6,  'Stockage',          1),
    (7,  'Périphériques',     NULL),
    (8,  'Écrans',            7),
    (9,  'Claviers',          7),
    (10, 'Souris',            7);

INSERT INTO marque (id, nom) VALUES
    (1, 'ASUS'),
    (2, 'MSI'),
    (3, 'AMD'),
    (4, 'Intel'),
    (5, 'Corsair'),
    (6, 'Samsung'),
    (7, 'Kingston'),
    (8, 'Logitech');

-- Prix hors taxe en euros, TVA en %
INSERT INTO produit (id, nom, description, prix_ht, tva, stock, image_url, marque_id, categorie_id, date_ajout) VALUES
    (1,  'AMD Ryzen 7 9800X3D', 'Le meilleur processeur pour le gaming avec la technologie 3D V-Cache.',
         550, 20, 15, 'produits/processeur.svg', 3, 2, '2026-05-28 07:50:56'),
    (2,  'Intel Core i9-14900K', 'Processeur ultra puissant avec 24 cœurs pour le streaming et le montage.',
         620, 20, 8, 'produits/processeur.svg', 4, 2, '2026-05-28 07:51:10'),
    (3,  'ASUS ROG Strix RTX 4080 SUPER', 'Carte graphique surpuissante pour jouer en 4K avec du Ray Tracing au max.',
         1150, 20, 5, 'produits/carte-graphique.svg', 1, 3, '2026-05-28 07:52:30'),
    (4,  'MSI RTX 4070 Gaming X Trio', 'Le parfait compromis pour jouer en 1440p à plus de 144 Hz.',
         680, 20, 0, 'produits/carte-graphique.svg', 2, 3, '2026-05-28 07:53:45'),
    (5,  'AMD Ryzen 5 9600X', 'Processeur 6 cœurs Zen 5, idéal pour une configuration gaming équilibrée.',
         230, 20, 22, 'produits/processeur.svg', 3, 2, '2026-09-02 10:00:00'),
    (6,  'MSI MAG B650 TOMAHAWK WIFI', 'Carte mère AM5 au format ATX avec DDR5 et Wi-Fi 6E.',
         190, 20, 10, 'produits/carte-mere.svg', 2, 4, '2026-09-10 10:00:00'),
    (7,  'ASUS ROG STRIX Z790-F GAMING WIFI', 'Carte mère Intel LGA 1700 haut de gamme avec DDR5, PCIe 5.0 et Wi-Fi 6E.',
         380, 20, 4, 'produits/carte-mere.svg', 1, 4, '2026-09-18 10:00:00'),
    (8,  'Corsair Vengeance DDR5 32 Go (2 x 16 Go) 6000 MHz', 'Kit mémoire DDR5 CL30 compatible AMD EXPO et Intel XMP.',
         105, 20, 30, 'produits/memoire.svg', 5, 5, '2026-09-25 10:00:00'),
    (9,  'Samsung 990 PRO 2 To', 'SSD NVMe PCIe 4.0 jusqu''à 7 450 Mo/s en lecture pour des chargements quasi instantanés.',
         165, 20, 18, 'produits/stockage.svg', 6, 6, '2026-10-01 10:00:00'),
    (10, 'Kingston NV3 1 To', 'SSD NVMe PCIe 4.0 abordable, jusqu''à 6 000 Mo/s en lecture.',
         58, 20, 40, 'produits/stockage.svg', 7, 6, '2026-10-08 10:00:00'),
    (11, 'Samsung Odyssey G5 27"', 'Écran gaming incurvé 27 pouces QHD 165 Hz avec un temps de réponse de 1 ms.',
         230, 20, 7, 'produits/ecran.svg', 6, 8, '2026-10-15 10:00:00'),
    (12, 'ASUS TUF Gaming VG27AQ3A', 'Écran 27 pouces Fast IPS QHD 180 Hz compatible G-Sync.',
         210, 20, 3, 'produits/ecran.svg', 1, 8, '2026-10-22 10:00:00'),
    (13, 'Logitech G915 X TKL', 'Clavier mécanique sans fil ultra-plat avec rétroéclairage RGB LIGHTSYNC.',
         190, 20, 9, 'produits/clavier.svg', 8, 9, '2026-10-29 10:00:00'),
    (14, 'Corsair K70 RGB PRO', 'Clavier mécanique gaming avec switches Cherry MX et repose-poignets amovible.',
         140, 20, 6, 'produits/clavier.svg', 5, 9, '2026-11-03 10:00:00'),
    (15, 'Logitech G PRO X SUPERLIGHT 2', 'Souris gaming sans fil de 60 g avec capteur HERO 2.',
         135, 20, 14, 'produits/souris.svg', 8, 10, '2026-11-08 10:00:00');

-- Les id ont été donnés à la main : on remet les compteurs SERIAL à jour
-- (sinon le prochain INSERT essaierait de réutiliser l'id 1)
SELECT setval('role_id_seq',      (SELECT MAX(id) FROM role));
SELECT setval('users_id_seq',     (SELECT MAX(id) FROM users));
SELECT setval('categorie_id_seq', (SELECT MAX(id) FROM categorie));
SELECT setval('marque_id_seq',    (SELECT MAX(id) FROM marque));
SELECT setval('produit_id_seq',   (SELECT MAX(id) FROM produit));
