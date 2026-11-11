package nc.allan.ldnc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Une ligne du panier (table "ligne_panier") : un produit et sa quantité.
 * La clé primaire est composée de (panier_id, produit_id) : voir LignePanierId.
 */
@Entity
@Table(name = "ligne_panier")
@Getter
@Setter
@NoArgsConstructor
public class LignePanier {

    @EmbeddedId
    @JsonIgnore
    private LignePanierId id = new LignePanierId();

    // @MapsId : la colonne panier_id sert à la fois de clé étrangère et de partie de la clé primaire
    @ManyToOne
    @MapsId("panierId")
    @JoinColumn(name = "panier_id")
    @JsonIgnore
    private Panier panier;

    @ManyToOne
    @MapsId("produitId")
    @JoinColumn(name = "produit_id")
    private Produit produit;

    private Integer quantite;

    /** Prix TTC du produit multiplié par la quantité. */
    public double getSousTotalTtc() {
        return Math.round(produit.getPrixTtc() * quantite * 100) / 100.0;
    }
}
