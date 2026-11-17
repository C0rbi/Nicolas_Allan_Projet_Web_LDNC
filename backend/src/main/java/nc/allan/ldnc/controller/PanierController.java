package nc.allan.ldnc.controller;

import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.entity.Panier;
import nc.allan.ldnc.service.PanierService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Panier de l'utilisateur connecté (token JWT obligatoire, voir SecurityConfig).
 * "Principal" représente l'utilisateur connecté : getName() renvoie son email (lu dans le token).
 */
@RestController
@RequestMapping("/api/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;

    // GET /api/panier
    @GetMapping
    public Panier voir(Principal utilisateur) {
        return panierService.getPanier(utilisateur.getName());
    }

    // POST /api/panier/3?quantite=1 : ajoute le produit 3
    @PostMapping("/{produitId}")
    public Panier ajouter(Principal utilisateur, @PathVariable Integer produitId,
                          @RequestParam(defaultValue = "1") int quantite) {
        return panierService.ajouter(utilisateur.getName(), produitId, quantite);
    }

    // PUT /api/panier/3?quantite=2 : la quantité du produit 3 passe à 2
    @PutMapping("/{produitId}")
    public Panier modifierQuantite(Principal utilisateur, @PathVariable Integer produitId,
                                   @RequestParam int quantite) {
        return panierService.modifierQuantite(utilisateur.getName(), produitId, quantite);
    }

    // DELETE /api/panier/3 : retire le produit 3
    @DeleteMapping("/{produitId}")
    public Panier retirer(Principal utilisateur, @PathVariable Integer produitId) {
        return panierService.retirer(utilisateur.getName(), produitId);
    }

    // DELETE /api/panier : vide le panier
    @DeleteMapping
    public Panier vider(Principal utilisateur) {
        return panierService.vider(utilisateur.getName());
    }
}
