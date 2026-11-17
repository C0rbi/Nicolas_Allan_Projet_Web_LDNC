package nc.allan.ldnc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Clé primaire composée de la table ligne_panier : (panier_id, produit_id).
 * JPA exige qu'une clé composée soit Serializable et définisse equals() et hashCode().
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LignePanierId implements Serializable {

    @Column(name = "panier_id")
    private Integer panierId;

    @Column(name = "produit_id")
    private Integer produitId;
}
