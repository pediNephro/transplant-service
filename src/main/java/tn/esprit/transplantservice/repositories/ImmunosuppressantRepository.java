package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.Immunosuppressant;

public interface ImmunosuppressantRepository extends JpaRepository<Immunosuppressant, Long> {
}
