package nc.allan.ldnc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity 
@Table(name = "produit") 
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class Produit {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "prix_ht", nullable = false)
    private Double prix_ht;

    @Column(name = "tva")
    private Double tva;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "marque_id")
    private Long marque_id;

    @Column(name = "categorie_id")
    private Long categorie_id;

    @Column(name = "date_ajout", insertable = false, updatable = false)
    private LocalDateTime dateAjout;
}