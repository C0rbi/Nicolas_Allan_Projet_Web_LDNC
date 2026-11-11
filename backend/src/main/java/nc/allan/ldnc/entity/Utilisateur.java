package nc.allan.ldnc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Un compte du site (table "users").
 * Cette classe n'est jamais renvoyée telle quelle au navigateur (voir UtilisateurDto) :
 * le mot de passe haché ne doit pas sortir de l'API.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;

    private String prenom;

    private String email;

    // Empreinte BCrypt du mot de passe (jamais le mot de passe en clair)
    @Column(name = "mot_de_passe")
    private String motDePasse;

    @Column(name = "date_creation", insertable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Association N,N avec la table role, via la table user_role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
