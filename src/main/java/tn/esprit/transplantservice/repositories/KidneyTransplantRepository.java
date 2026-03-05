package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.KidneyTransplant;

public interface KidneyTransplantRepository extends JpaRepository<KidneyTransplant, Long> {
}
