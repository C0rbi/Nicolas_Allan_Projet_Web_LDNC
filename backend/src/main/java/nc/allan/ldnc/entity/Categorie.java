package nc.allan.ldnc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Une catégorie (table "categorie").
 * parentId vaut null pour une catégorie principale (ex. Composants)
 * et contient l'id de la catégorie parente pour une sous-catégorie (ex. Processeurs).
 */
@Entity
@Table(name = "categorie")
@Getter
@Setter
@NoArgsConstructor
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;

    @Column(name = "parent_id")
    private Integer parentId;
}
