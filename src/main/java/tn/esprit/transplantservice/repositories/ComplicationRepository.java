package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.Complication;

import java.util.List;

public interface ComplicationRepository extends JpaRepository<Complication, Long> {
    List<Complication> findByTransplantId(Long transplantId);
}
