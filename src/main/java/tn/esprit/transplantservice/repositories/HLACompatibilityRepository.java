package tn.esprit.transplantservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.HLACompatibility;

public interface HLACompatibilityRepository extends JpaRepository<HLACompatibility, Long> {
}
