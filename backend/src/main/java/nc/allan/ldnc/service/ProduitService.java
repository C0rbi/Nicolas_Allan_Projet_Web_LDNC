package nc.allan.ldnc.service;

import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.entity.Produit;
import nc.allan.ldnc.repository.ProduitRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Catalogue : recherche (tout le monde) et gestion des produits (administrateur). */
@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;

    public List<Produit> rechercher(String recherche, Integer categorieId, Integer marqueId, Double prixMax, String tri) {
        // "ryzen" devient "%ryzen%" : le produit doit CONTENIR le texte (sans tenir compte des majuscules)
        String motif = "%" + (recherche == null ? "" : recherche.trim().toLowerCase()) + "%";
        return produitRepository.rechercher(motif, categorieId, marqueId, prixMax, choisirTri(tri));
    }

    private Sort choisirTri(String tri) {
        if (tri == null) {
            return Sort.by("nom");
        }
        return switch (tri) {
            case "prix-croissant" -> Sort.by("prixHt").ascending();
            case "prix-decroissant" -> Sort.by("prixHt").descending();
            case "nouveautes" -> Sort.by("dateAjout").descending();
            default -> Sort.by("nom");
        };
    }

    public Produit trouver(Integer id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable"));
    }

    public Produit creer(Produit produit) {
        produit.setId(null);   // l'id est donné par la base (SERIAL)
        Produit enregistre = produitRepository.save(produit);
        // On relit le produit en base pour renvoyer la marque et la catégorie complètes
        return trouver(enregistre.getId());
    }

    public Produit modifier(Integer id, Produit produit) {
        trouver(id);           // erreur 404 si le produit n'existe pas
        produit.setId(id);     // save() avec un id existant fait un UPDATE
        produitRepository.save(produit);
        return trouver(id);
    }

    public void supprimer(Integer id) {
        produitRepository.delete(trouver(id));
    }
}
