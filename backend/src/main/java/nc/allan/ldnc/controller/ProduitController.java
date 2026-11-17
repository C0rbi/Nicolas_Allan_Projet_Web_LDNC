package nc.allan.ldnc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.entity.Produit;
import nc.allan.ldnc.service.ProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    // GET /api/produits?recherche=ryzen&categorieId=2&marqueId=3&prixMax=700&tri=prix-croissant
    // Tous les paramètres sont facultatifs
    @GetMapping
    public List<Produit> rechercher(@RequestParam(required = false) String recherche,
                                    @RequestParam(required = false) Integer categorieId,
                                    @RequestParam(required = false) Integer marqueId,
                                    @RequestParam(required = false) Double prixMax,
                                    @RequestParam(required = false) String tri) {
        return produitService.rechercher(recherche, categorieId, marqueId, prixMax, tri);
    }

    // GET /api/produits/3
    @GetMapping("/{id}")
    public Produit detail(@PathVariable Integer id) {
        return produitService.trouver(id);
    }

    // --- Réservé aux administrateurs (voir SecurityConfig) ---

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produit creer(@Valid @RequestBody Produit produit) {
        return produitService.creer(produit);
    }

    @PutMapping("/{id}")
    public Produit modifier(@PathVariable Integer id, @Valid @RequestBody Produit produit) {
        return produitService.modifier(id, produit);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        produitService.supprimer(id);
    }
}
