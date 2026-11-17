package nc.allan.ldnc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Un produit du catalogue (table "produit").
 * Les annotations @NotBlank, @NotNull... vérifient les données envoyées par l'administrateur.
 */
@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères")
    private String nom;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    @NotNull(message = "Le prix HT est obligatoire")
    @PositiveOrZero(message = "Le prix HT ne peut pas être négatif")
    @Column(name = "prix_ht")
    private Double prixHt;

    @NotNull(message = "La TVA est obligatoire")
    @PositiveOrZero(message = "La TVA ne peut pas être négative")
    private Double tva;

    @NotNull(message = "Le stock est obligatoire")
    @PositiveOrZero(message = "Le stock ne peut pas être négatif")
    private Integer stock;

    @Size(max = 255, message = "Le chemin de l'image ne doit pas dépasser 255 caractères")
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "marque_id")
    private Marque marque;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    // Rempli par la base (DEFAULT CURRENT_TIMESTAMP) : Java ne l'écrit jamais
    @Column(name = "date_ajout", insertable = false, updatable = false)
    private LocalDateTime dateAjout;

    /** Prix TTC arrondi au centime. Envoyé dans le JSON sous le nom "prixTtc". */
    public double getPrixTtc() {
        if (prixHt == null || tva == null) {
            return 0;
        }
        return Math.round(prixHt * (1 + tva / 100) * 100) / 100.0;
    }
}
