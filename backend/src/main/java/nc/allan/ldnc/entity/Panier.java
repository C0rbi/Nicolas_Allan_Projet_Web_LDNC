package nc.allan.ldnc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Le panier d'un utilisateur (table "panier") : un seul panier par compte.
 * Les totaux sont calculés à partir des lignes et envoyés dans le JSON
 * (nombreArticles, totalHt, totalTtc).
 */
@Entity
@Table(name = "panier")
@Getter
@Setter
@NoArgsConstructor
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Propriétaire du panier (pas envoyé au navigateur)
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Utilisateur utilisateur;

    // cascade + orphanRemoval : ajouter ou retirer une ligne de cette liste suffit
    // pour que Hibernate l'insère ou la supprime dans la table ligne_panier
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id.produitId")
    private List<LignePanier> lignes = new ArrayList<>();

    public int getNombreArticles() {
        int nombre = 0;
        for (LignePanier ligne : lignes) {
            nombre += ligne.getQuantite();
        }
        return nombre;
    }

    public double getTotalHt() {
        double total = 0;
        for (LignePanier ligne : lignes) {
            total += ligne.getProduit().getPrixHt() * ligne.getQuantite();
        }
        return Math.round(total * 100) / 100.0;
    }

    public double getTotalTtc() {
        double total = 0;
        for (LignePanier ligne : lignes) {
            total += ligne.getSousTotalTtc();
        }
        return Math.round(total * 100) / 100.0;
    }
}
