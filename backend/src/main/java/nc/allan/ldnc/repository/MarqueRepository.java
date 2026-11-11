package nc.allan.ldnc.repository;

import nc.allan.ldnc.entity.Marque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarqueRepository extends JpaRepository<Marque, Integer> {
}
