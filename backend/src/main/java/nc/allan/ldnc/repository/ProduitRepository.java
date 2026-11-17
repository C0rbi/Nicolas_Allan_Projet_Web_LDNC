package nc.allan.ldnc.repository;

import nc.allan.ldnc.entity.Produit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    /**
     * Recherche multicritère (requête JPQL). Un critère à null est ignoré.
     * - motif : texte cherché dans le nom ou la description, au format LIKE (ex. "%ryzen%")
     * - categorieId : une catégorie principale (ex. Composants) inclut ses sous-catégories
     * - prixMax : prix TTC maximum
     * - tri : ordre des résultats (prix croissant, nouveautés...)
     */
    @Query("""
            SELECT p FROM Produit p
            LEFT JOIN p.categorie c
            WHERE (LOWER(p.nom) LIKE :motif OR LOWER(p.description) LIKE :motif)
              AND (:categorieId IS NULL OR c.id = :categorieId OR c.parentId = :categorieId)
              AND (:marqueId IS NULL OR p.marque.id = :marqueId)
              AND (:prixMax IS NULL OR p.prixHt * (1 + p.tva / 100) <= :prixMax)
            """)
    List<Produit> rechercher(@Param("motif") String motif,
                             @Param("categorieId") Integer categorieId,
                             @Param("marqueId") Integer marqueId,
                             @Param("prixMax") Double prixMax,
                             Sort tri);
}
