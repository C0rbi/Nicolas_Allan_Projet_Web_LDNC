package nc.allan.ldnc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Une marque (table "marque"), ex. ASUS, AMD. */
@Entity
@Table(name = "marque")
@Getter
@Setter
@NoArgsConstructor
public class Marque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
}
