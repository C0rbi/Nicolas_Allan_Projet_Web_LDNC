package nc.allan.ldnc.repository;

import nc.allan.ldnc.entity.Panier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Integer> {

    // Panier dont l'utilisateur a cet email (jointure panier -> users faite par Spring Data)
    Optional<Panier> findByUtilisateurEmail(String email);
}
