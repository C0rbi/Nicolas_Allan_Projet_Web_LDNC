package nc.allan.ldnc.repository;

import nc.allan.ldnc.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Spring Data écrit la requête à partir du nom de la méthode : SELECT ... WHERE nom = ?
    Optional<Role> findByNom(String nom);
}
