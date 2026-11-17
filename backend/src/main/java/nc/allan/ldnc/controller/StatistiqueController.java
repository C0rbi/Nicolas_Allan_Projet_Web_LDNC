package nc.allan.ldnc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Statistiques du stock pour la page Gestion (administrateurs seulement, voir SecurityConfig).
 * Les calculs sont faits par la vue SQL vue_stock_par_categorie (database/02_sql_avance.sql).
 */
@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
public class StatistiqueController {

    // JdbcTemplate exécute directement une requête SQL : pratique pour lire une vue, sans créer d'entité JPA
    private final JdbcTemplate jdbcTemplate;

    // GET /api/statistiques : une ligne par catégorie
    // [{ "categorie": "Processeurs", "nb_produits": 3, "nb_en_rupture": 0, "nb_articles": 45, "valeur_stock_ht": 18270.00 }, ...]
    @GetMapping
    public List<Map<String, Object>> stockParCategorie() {
        return jdbcTemplate.queryForList("SELECT * FROM vue_stock_par_categorie");
    }
}
