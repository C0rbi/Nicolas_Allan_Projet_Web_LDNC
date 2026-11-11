package nc.allan.ldnc.service;

import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.entity.LignePanier;
import nc.allan.ldnc.entity.Panier;
import nc.allan.ldnc.entity.Produit;
import nc.allan.ldnc.entity.Utilisateur;
import nc.allan.ldnc.repository.PanierRepository;
import nc.allan.ldnc.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Gestion du panier de l'utilisateur connecté.
 *
 * @Transactional : chaque méthode s'exécute dans une transaction. À la fin de la méthode,
 * Hibernate enregistre tout seul les changements faits sur le panier et ses lignes
 * (ligne ajoutée = INSERT, quantité changée = UPDATE, ligne retirée = DELETE).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PanierService {

    private final PanierRepository panierRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitService produitService;

    /** Renvoie le panier de l'utilisateur, en le créant s'il n'existe pas encore. */
    public Panier getPanier(String email) {
        Optional<Panier> panierExistant = panierRepository.findByUtilisateurEmail(email);
        if (panierExistant.isPresent()) {
            return panierExistant.get();
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Compte introuvable"));
        Panier panier = new Panier();
        panier.setUtilisateur(utilisateur);
        return panierRepository.save(panier);
    }

    /** Ajoute un produit ; s'il est déjà dans le panier, sa quantité augmente. */
    public Panier ajouter(String email, Integer produitId, int quantite) {
        Panier panier = getPanier(email);
        Produit produit = produitService.trouver(produitId);
        LignePanier ligne = trouverLigne(panier, produitId);

        if (ligne == null) {
            verifierQuantite(produit, quantite);
            ligne = new LignePanier();
            ligne.setPanier(panier);
            ligne.setProduit(produit);
            ligne.setQuantite(quantite);
            panier.getLignes().add(ligne);
        } else {
            int nouvelleQuantite = ligne.getQuantite() + quantite;
            verifierQuantite(produit, nouvelleQuantite);
            ligne.setQuantite(nouvelleQuantite);
        }
        return panier;
    }

    /** Remplace la quantité d'un produit du panier. */
    public Panier modifierQuantite(String email, Integer produitId, int quantite) {
        Panier panier = getPanier(email);
        LignePanier ligne = trouverLigne(panier, produitId);
        if (ligne == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce produit n'est pas dans le panier");
        }
        verifierQuantite(ligne.getProduit(), quantite);
        ligne.setQuantite(quantite);
        return panier;
    }

    public Panier retirer(String email, Integer produitId) {
        Panier panier = getPanier(email);
        LignePanier ligne = trouverLigne(panier, produitId);
        if (ligne != null) {
            panier.getLignes().remove(ligne);   // orphanRemoval : la ligne est supprimée de la base
        }
        return panier;
    }

    public Panier vider(String email) {
        Panier panier = getPanier(email);
        panier.getLignes().clear();
        return panier;
    }

    private LignePanier trouverLigne(Panier panier, Integer produitId) {
        for (LignePanier ligne : panier.getLignes()) {
            if (ligne.getProduit().getId().equals(produitId)) {
                return ligne;
            }
        }
        return null;
    }

    /** Règles métier : au moins 1 article, et pas plus que le stock disponible. */
    private void verifierQuantite(Produit produit, int quantite) {
        if (quantite < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La quantité doit être d'au moins 1");
        }
        if (quantite > produit.getStock()) {
            String message = produit.getStock() == 0
                    ? "Ce produit est en rupture de stock"
                    : "Stock insuffisant : il reste " + produit.getStock() + " exemplaire(s)";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
