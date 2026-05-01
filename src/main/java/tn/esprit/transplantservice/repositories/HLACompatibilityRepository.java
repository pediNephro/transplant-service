package tn.esprit.transplantservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.transplantservice.entities.HLACompatibility;
import tn.esprit.transplantservice.entities.RejectionEpisode;

public interface HLACompatibilityRepository extends JpaRepository<HLACompatibility, Long> {
    List<HLACompatibility> findByTransplantId(Long transplantId);

}
