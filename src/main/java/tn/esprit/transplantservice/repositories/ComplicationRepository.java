package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.Complication;

public interface ComplicationRepository extends JpaRepository<Complication, Long> {
}
